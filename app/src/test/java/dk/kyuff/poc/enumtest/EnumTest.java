package dk.kyuff.poc.enumtest;

import dk.kyuff.poc.EnumSerializer;
import org.junit.Before;
import org.junit.Test;

public class EnumTest {

    private EnumSerializer serializer;

    @Before
    public void setUp() throws Exception {
        serializer = new EnumSerializer("src/test/resources/enums");
    }

    @Test
    public void testValidateCurrentReleaseFiles() throws Exception {
        serializer.assertValidReleaseStore();
    }
}
