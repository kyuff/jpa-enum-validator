package dk.kyuff.poc.accounts;

import dk.kyuff.poc.shared.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
public class AccountEntity extends AbstractEntity {

    @MapKeyEnumerated(EnumType.STRING)
    private AccountType type;


}
