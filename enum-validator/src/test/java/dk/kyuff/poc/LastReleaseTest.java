package dk.kyuff.poc;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.kyuff.poc.model.Entity1;
import dk.kyuff.poc.model.Entity2;
import dk.kyuff.poc.model.Enum1;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LastReleaseTest {

    @Test
    public void testSerialize() throws Exception {
        // setup
        ObjectMapper mapper = new ObjectMapper();

        // execute
        LastRelease release = mapper.readValue(getClass().getClassLoader().getResourceAsStream("LastRelease.example.json"), LastRelease.class);

        // verify
        assertNotNull(release);
        assertEquals(Enum1.class, release.getEnumType());
        assertEquals(Arrays.asList("A", "B", "C"), release.getEnumFieldsInRelease());
        assertEquals(Arrays.asList(Entity1.class, Entity2.class), release.getClasses());
        assertEquals(2, release.getChanges().size());


    }
}