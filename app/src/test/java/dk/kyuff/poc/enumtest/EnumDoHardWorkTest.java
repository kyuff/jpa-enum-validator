package dk.kyuff.poc.enumtest;

import dk.kyuff.poc.EnumSerializer;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Persistence;
import javax.persistence.metamodel.Metamodel;

public class EnumDoHardWorkTest {
    Metamodel accounts;
    Metamodel users;
    EnumSerializer serializer;

    @Before
    public void setUp() throws Exception {
        accounts = Persistence.createEntityManagerFactory("accounts").getMetamodel();
        users = Persistence.createEntityManagerFactory("users").getMetamodel();
        serializer = new EnumSerializer("src/test/resources/enums", accounts, users);

    }

    @Test
    public void testIsAllChangesHandled() throws Exception {
        serializer.assertAllChangesHandled();
    }

    public void testSerialization() throws Exception {
        serializer.prepareRelease();
        serializer.addModel(accounts);
        serializer.addModel(users);
        serializer.store();
    }
}
