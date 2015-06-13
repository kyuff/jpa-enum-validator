package dk.kyuff.poc.accounts;

import dk.kyuff.poc.shared.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.MapKeyEnumerated;

@Entity
public class AccountEntity extends AbstractEntity {


    @MapKeyEnumerated(EnumType.STRING)
    private AccountType type;


}
