package dk.kyuff.poc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class EnumSerializer {

    private final Path path;
    private final Map<Class<Enum<?>>, LastRelease> releaseMap;
    private final ObjectMapper mapper;
    private final Validator validator;
    private final Metamodel[] models;

    public EnumSerializer(String path, Metamodel... models) {
        this.models = models;
        this.path = Paths.get(path);
        this.releaseMap = new ConcurrentHashMap<>();
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();

    }

    public void prepareRelease() {
        try {
            Files.newDirectoryStream(this.path).forEach((file) -> {
                try {
                    if (file.endsWith(".json")) {
                        Files.delete(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addModel(Metamodel meta) {
        meta.getEntities().forEach(this::parseEntity);

    }

    private void parseEntity(EntityType<?> entityType) {
        entityType.getAttributes().parallelStream()
                .filter(a -> a.getJavaType().isEnum())
                .forEach(a -> {
                    @SuppressWarnings("unchecked")
                    Class<Enum<?>> attrType = (Class<Enum<?>>) a.getJavaType();
                    LastRelease lastRelease = releaseMap.computeIfAbsent(attrType, LastRelease::new);
                    for (Enum<?> constants : attrType.getEnumConstants()) {
                        lastRelease.getEnumFieldsInRelease().add(constants.name());
                    }
                    lastRelease.getClasses().add(entityType.getJavaType());
                });
    }

    public void store() {
        releaseMap.values().forEach(this::store);
    }

    private void store(LastRelease lastRelease) {
        Path file = Paths.get(path.toString(), lastRelease.getEnumType().getCanonicalName() + ".json");
        try {
            Writer writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"));
            mapper.writeValue(writer, lastRelease);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Stream<LastRelease> readLastReleases() {
        try {
            return Files.find(path, 1, (candidate, attr) -> candidate.toString().endsWith("json"))
                    .map(this::assertCorrectJsonSyntax);
        } catch (IOException e) {
            fail("could not read configuration: " + e.getMessage());
        }
        return Stream.empty();
    }

    public boolean assertValidReleaseStore() {
        try {
            Files.find(path, 1, (candidate, attr) -> candidate.toString().endsWith("json")).forEach(file -> {
                LastRelease lastRelease = assertCorrectJsonSyntax(file);
                assertValidContent(lastRelease, file);
            });
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void assertValidContent(LastRelease lastRelease, Path file) {
        Set<ConstraintViolation<LastRelease>> errors = validator.validate(lastRelease);
        String errorMessage = errors.stream()
                .map(violation -> violation.getPropertyPath() + " - " + violation.getMessage())
                .reduce("Found errors in " + file + "\n", (a, b) -> a + "\n" + b);
        assertEquals(errorMessage, 0, errors.size());
    }

    private LastRelease assertCorrectJsonSyntax(Path file) {
        try {
            LastRelease lastRelease = mapper.readValue(file.toFile(), LastRelease.class);
            assertNotNull(file + " was unmarshalled as null", lastRelease);
            return lastRelease;
        } catch (IOException e) {
            if ((e.getCause() != null) && (e.getCause() instanceof ClassNotFoundException)) {
                fail("Could not unmarshal due to a classNotFoundException. Have a class been refactored and the JSON files needs to be updated: " + e.getMessage());
            }
            fail("Could not unmarshal " + file + ": " + e.getMessage());
        }
        return null;
    }

    public void assertAllChangesHandled() {
        readLastReleases().forEach(lastRelease -> releaseMap.put(lastRelease.getEnumType(), lastRelease));

        for (Metamodel model : models) {
            model.getEntities().forEach(entityType -> {
                entityType.getAttributes().parallelStream()
                        .filter(a -> a.getJavaType().isEnum())
                        .forEach(a -> {
                            @SuppressWarnings("unchecked")
                            Class<Enum<?>> attrType = (Class<Enum<?>>) a.getJavaType();
                            Set<String> fieldsInCurrentVersion = new HashSet<>();
                            for (Enum<?> anEnum : attrType.getEnumConstants()) {
                                fieldsInCurrentVersion.add(anEnum.name());
                            }
                            LastRelease lastRelease = releaseMap.get(attrType);
                            if (lastRelease != null) {
                                List<String> unhandledFields = lastRelease.getEnumFieldsInRelease().stream()
                                        .filter(fieldInRelease -> !fieldsInCurrentVersion.contains(fieldInRelease))
                                        .filter(deletedFieldInRelease -> !releaseHaveChangeThatHandles(lastRelease, deletedFieldInRelease))
                                        .collect(Collectors.toList());
                                assertEquals("Deleted fields are not handled " + unhandledFields + " in Enum Type: " + lastRelease.getEnumType(), 0, unhandledFields.size());
                            }
                        });
            });
        }
    }

    private boolean releaseHaveChangeThatHandles(LastRelease lastRelease, String deletedFieldInRelease) {
        return lastRelease.getChanges().stream().filter(change -> change.getField().equals(deletedFieldInRelease)).findFirst().isPresent();
    }
}
