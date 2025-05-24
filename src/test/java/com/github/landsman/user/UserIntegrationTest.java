package com.github.landsman.user;

import com.github.landsman.config.TestApplication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback(false)
class UserIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testIdGeneration() {
        User user = new User();
        entityManager.persist(user);
        entityManager.flush();

        assertNotNull(user.getId());
        assertTrue(user.getId().startsWith("u_"));
    }

    @Test
    public void testDatabaseUniqueConstraint() {
        // Save the first user using EntityManager
        User user1 = new User();
        entityManager.persist(user1);
        entityManager.flush(); // Flush to ensure the first user is persisted

        // Get the ID of the first user
        String userId = user1.getId();
        assertNotNull(userId);

        // Try to save another user with the same ID using EntityManager
        User user2 = new User();
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

        List<User> users = new ArrayList<>();
        for (int i = 0; i < howManyUsers; i++) {
            User user = new User();
            entityManager.persist(user);
            users.add(user);
        }
        entityManager.flush();

        // Count the number of users in the database
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();
        assertEquals(howManyUsers, count);

        // verify uniqueness via a hash set as well as database
        Set<String> generatedIds = new HashSet<>();
        for (User savedUser : users) {
            String id = savedUser.getId();
            assertTrue(id.startsWith("u_"));
            assertTrue(generatedIds.add(id), "Generated ID should be unique");
        }

        assertEquals(howManyUsers, generatedIds.size());
    }
}
