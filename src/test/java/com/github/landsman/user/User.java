package com.github.landsman.user;

import com.github.landsman.IdTypeId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * User entity for testing TypeIdHibernate generator.
 */
@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @IdTypeId(prefix = "u")
    private String id;

    // Default constructor required by JPA
    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
