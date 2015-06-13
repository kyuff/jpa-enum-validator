package dk.kyuff.poc.accounts;

import dk.kyuff.poc.shared.Status;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;

import static org.junit.Assert.*;

public class AccountDaoTest {

    private AccountDao dao;

    @Before
    public void setUp() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("accounts");
        dao = new AccountDao();
        dao.entityManager = emf.createEntityManager();

    }

    @Test
    public void testPersist() throws Exception {
        // setup
        AccountEntity entity = new AccountEntity();
        entity.setStatus(Status.CREATED);

        // execute
        dao.persist(entity);

        // verify
        List<AccountEntity> accounts = dao.readAll();
        System.out.println(accounts);
        assertNotEquals(0, entity.getId());
    }
}