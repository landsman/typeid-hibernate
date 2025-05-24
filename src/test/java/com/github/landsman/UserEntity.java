package com.github.landsman;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * User entity for testing TypeIdHibernate generator.
 */
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(unique = true) // This ensures database-level uniqueness
    @TypeIdHibernate(prefix = "u")
    private String id;

    // Default constructor required by JPA
    public UserEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
