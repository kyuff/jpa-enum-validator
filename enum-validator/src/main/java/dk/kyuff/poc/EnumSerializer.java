package dk.kyuff.poc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumSerializer {

    private final Path path;
    private final Map<Class<Enum<?>>, LastRelease> releaseMap;
    private final ObjectMapper mapper;

    public EnumSerializer(String path) {
        this.path = Paths.get(path);
        this.releaseMap = new ConcurrentHashMap<>();
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);

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
}
