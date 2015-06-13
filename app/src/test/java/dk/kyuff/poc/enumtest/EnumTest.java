package dk.kyuff.poc.enumtest;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

public class EnumTest {

    Metamodel accounts;

    @Before
    public void setUp() throws Exception {
        accounts = Persistence.createEntityManagerFactory("accounts").getMetamodel();

    }

    @Test
    public void testEnums() throws Exception {
        assertValidMetaModel(accounts);
    }

    private void assertValidMetaModel(Metamodel metamodel) {
        for (EntityType<?> entityType : metamodel.getEntities()) {
            System.out.println(entityType.getJavaType());
            entityType.getAttributes().forEach(attribute -> {
                Class<?> javaType = attribute.getJavaType();
                System.out.println("\tType: "+ javaType);
                if( javaType.isEnum() ) {
                    Object[] enumConstants = javaType.getEnumConstants();
                    System.out.println(enumConstants);
                }


            });
        }
    }
}
