package com.github.landsman.user;

import com.github.landsman.config.TestApplication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    public void testIdGeneration() {
        User user = new User();
        user = userRepository.save(user);

        assertNotNull(user.getId());
        assertTrue(user.getId().startsWith("u_"));
    }

    @Test
    @Rollback
    public void testDatabaseUniqueConstraint() {
        // Save the first user using UserRepository
        User user1 = new User();
        user1 = userRepository.save(user1);

        // Get the ID of the first user
        String userId = user1.getId();
        assertNotNull(userId);

        // Try to save another user with the same ID using EntityManager
        // We use EntityManager directly here because UserRepository might handle duplicates differently
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
        int batchSize = 100; // Save in smaller batches

        List<User> allUsers = new ArrayList<>();

        // Save users in batches
        for (int batch = 0; batch < howManyUsers / batchSize; batch++) {
            List<User> batchUsers = new ArrayList<>();
            for (int i = 0; i < batchSize; i++) {
                batchUsers.add(new User());
            }
            List<User> savedBatch = userRepository.saveAll(batchUsers);
            allUsers.addAll(savedBatch);
        }

        // Count the number of users in the database
        long count = userRepository.count();
        assertEquals(howManyUsers, count);

        // verify uniqueness via a hash set as well as database
        Set<String> generatedIds = new HashSet<>();
        for (User savedUser : allUsers) {
            String id = savedUser.getId();
            assertTrue(id.startsWith("u_"));
            assertTrue(generatedIds.add(id), "Generated ID should be unique");
        }

        assertEquals(howManyUsers, generatedIds.size());
    }
}
