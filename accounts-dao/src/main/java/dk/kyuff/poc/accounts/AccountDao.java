package dk.kyuff.poc.accounts;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class AccountDao {

    EntityManager entityManager;

    public AccountEntity persist(AccountEntity accountEntity) {
        entityManager.getTransaction().begin();
        entityManager.persist(accountEntity);
        entityManager.getTransaction().commit();
        return accountEntity;
    }

    @SuppressWarnings("unchecked")
    public List<AccountEntity> readAll() {
        Query query = entityManager.createQuery("SELECT a FROM AccountEntity a ORDER BY a.id");
        return (List<AccountEntity>) query.getResultList();
    }

}
