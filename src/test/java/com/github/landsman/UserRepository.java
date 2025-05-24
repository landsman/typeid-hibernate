package com.github.landsman;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for UserEntity.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    // Spring Data JPA will automatically implement basic CRUD operations
}