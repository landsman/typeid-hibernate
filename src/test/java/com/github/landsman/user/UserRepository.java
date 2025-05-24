package com.github.landsman.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for UserEntity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Spring Data JPA will automatically implement basic CRUD operations
}
