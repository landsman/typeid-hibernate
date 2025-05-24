package com.github.landsman;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = UserEntityIntegrationTest.TestConfig.class)
public class UserEntityIntegrationTest {

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackages = "com.github.landsman")
    @EnableJpaRepositories(basePackages = "com.github.landsman")
    @ComponentScan(basePackages = "com.github.landsman")
    static class TestConfig {
    }

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testIdGeneration() {
        UserEntity user = new UserEntity();
        UserEntity savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertTrue(savedUser.getId().startsWith("u_"));
    }

    @Test
    public void testDatabaseUniqueConstraint() {
        // Save the first user using EntityManager
        UserEntity user1 = new UserEntity();
        entityManager.persist(user1);
        entityManager.flush(); // Flush to ensure the first user is persisted

        // Get the ID of the first user
        String userId = user1.getId();
        assertNotNull(userId);

        // Try to save another user with the same ID using EntityManager
        UserEntity user2 = new UserEntity();
        user2.setId(userId); // Force the same ID

        // This should throw an exception due to unique constraint violation
        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(user2);
            entityManager.flush(); // Force the flush to trigger the constraint check
        });
    }

    @Test
    public void testMultipleIdsAreUnique() {
        int howManyUsers = 10000;

        List<UserEntity> users = new ArrayList<>();
        for (int i = 0; i < howManyUsers; i++) {
            UserEntity user = new UserEntity();
            users.add(user);
        }

        List<UserEntity> savedUsers = userRepository.saveAll(users);
        userRepository.flush();

        assertEquals(howManyUsers, userRepository.count());

        // verify uniqueness via a hash set as well as database
        Set<String> generatedIds = new HashSet<>();
        for (UserEntity savedUser : savedUsers) {
            String id = savedUser.getId();
            assertTrue(id.startsWith("u_"));
            assertTrue(generatedIds.add(id), "Generated ID should be unique");
        }

        assertEquals(howManyUsers, generatedIds.size());
    }
}
