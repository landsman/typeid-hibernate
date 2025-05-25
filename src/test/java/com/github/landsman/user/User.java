package com.github.landsman.user;

import com.github.landsman.IdTypeId;
import com.github.landsman.OptimizedTypeIdType;
import com.github.landsman.TypeId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

/**
 * User entity for testing TypeIdHibernate generator.
 */
@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @IdTypeId(prefix = "u")
    @Type(OptimizedTypeIdType.class)
    private TypeId id;

    // Default constructor required by JPA
    public User() {
    }

    public TypeId getId() {
        return id;
    }

    public void setId(TypeId id) {
        this.id = id;
    }
}
