package dk.kyuff.poc.users;

import dk.kyuff.poc.shared.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class UserEntity extends AbstractEntity {


    private String name;
    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;


}
