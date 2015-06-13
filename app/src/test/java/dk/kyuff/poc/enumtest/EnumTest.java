package dk.kyuff.poc.enumtest;

import dk.kyuff.poc.EnumSerializer;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Persistence;
import javax.persistence.metamodel.Metamodel;

public class EnumTest {

    Metamodel accounts;
    Metamodel users;
    EnumSerializer serializer;

    @Before
    public void setUp() throws Exception {
        accounts = Persistence.createEntityManagerFactory("accounts").getMetamodel();
        users = Persistence.createEntityManagerFactory("users").getMetamodel();
        serializer = new EnumSerializer("src/test/resources/enums");

    }

    @Test
    public void testSerialization() throws Exception {
        serializer.prepareRelease();
        serializer.addModel(accounts);
        serializer.addModel(users);
        serializer.store();
    }

}
