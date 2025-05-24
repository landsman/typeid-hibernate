package com.github.landsman.user;

import com.github.landsman.OptimizedTypeIdType;
import com.github.landsman.TypeIdHibernate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

/**
 * User entity for testing TypeIdHibernate generator.
 */
@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(unique = true) // This ensures database-level uniqueness
    @Type(OptimizedTypeIdType.class)
    @TypeIdHibernate(prefix = "u")
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
