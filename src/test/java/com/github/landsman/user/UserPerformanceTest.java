package com.github.landsman.user;

import com.github.landsman.config.TestApplication;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserPerformanceTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    public void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    public void testBulkInsertPerformance() {
        int batchSize = 100;
        int totalUsers = 10000;
        List<Long> batchTimes = new ArrayList<>();

        // Warm up
        List<User> warmupUsers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            warmupUsers.add(new User());
        }
        userRepository.saveAll(warmupUsers);

        // Test batched inserts
        for (int batch = 0; batch < totalUsers/batchSize; batch++) {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < batchSize; i++) {
                users.add(new User());
            }

            long startTime = System.nanoTime();
            userRepository.saveAll(users);
            long endTime = System.nanoTime();

            batchTimes.add((endTime - startTime) / 1_000_000L); // Convert to milliseconds
        }

        // Calculate and print statistics
        DoubleSummaryStatistics stats = batchTimes.stream()
                .mapToDouble(Long::doubleValue)
                .summaryStatistics();

        System.out.printf("""
                Batch Insert Performance (batch size: %d):
                Total Records: %d
                Average batch time: %.2f ms
                Min batch time: %.2f ms
                Max batch time: %.2f ms
                Total time: %.2f ms%n""",
                batchSize,
                totalUsers,
                stats.getAverage(),
                stats.getMin(),
                stats.getMax(),
                stats.getSum());

        // Count the number of users in the database
        long count = userRepository.count();
        assertEquals(totalUsers + 10, count); // Add 10 for the warm-up users
    }

    @Test
    public void testQueryPerformance() {
        // Prepare test data
        int testDataSize = 10000;
        List<User> users = new ArrayList<>();
        for (int i = 0; i < testDataSize; i++) {
            users.add(new User());
        }
        users = userRepository.saveAll(users);

        // Test different query patterns
        Map<String, Long> queryTimes = new HashMap<>();

        // Test 1: Find by ID
        String randomId = users.get(new Random().nextInt(users.size())).getId().getValue();
        long startTime = System.nanoTime();
        userRepository.findById(randomId);
        queryTimes.put("Find by ID", (System.nanoTime() - startTime) / 1_000_000L);

        // Test 2: Find all (with limit)
        startTime = System.nanoTime();
        userRepository.findAll(PageRequest.of(0, 100));
        queryTimes.put("Find all (limit 100)", (System.nanoTime() - startTime) / 1_000_000L);

        // Test 3: ID prefix search (using EntityManager as JpaRepository doesn't have a direct method for LIKE queries)
        startTime = System.nanoTime();
        entityManager.createQuery("SELECT u FROM User u WHERE u.id LIKE :prefix", User.class)
                .setParameter("prefix", "u_%")
                .setMaxResults(100)
                .getResultList();
        queryTimes.put("ID prefix search", (System.nanoTime() - startTime) / 1_000_000L);

        // Print results
        System.out.println("\nQuery Performance Results:");
        queryTimes.forEach((queryType, time) ->
                System.out.printf("%s: %d ms%n", queryType, time));
    }

    @Test
    public void testConcurrentInsertPerformance() throws InterruptedException {
        int threadsCount = 2;
        int recordsPerThread = 500;
        CountDownLatch latch = new CountDownLatch(threadsCount);
        List<Future<List<Long>>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);

        // Create a shared UserRepository reference
        final UserRepository sharedUserRepository = userRepository;
        // Create a shared TransactionManager reference
        final PlatformTransactionManager sharedTransactionManager = transactionManager;

        try {
            // Create and submit tasks
            for (int i = 0; i < threadsCount; i++) {
                futures.add(executorService.submit(() -> {
                    List<Long> threadTimes = new ArrayList<>();
                    // Create a new TransactionTemplate for each thread
                    TransactionTemplate transactionTemplate = new TransactionTemplate(sharedTransactionManager);

                    // Execute in a transaction
                    transactionTemplate.execute(status -> {
                        try {
                            for (int j = 0; j < recordsPerThread; j++) {
                                long startTime = System.nanoTime();
                                User user = new User();
                                sharedUserRepository.save(user);
                                threadTimes.add((System.nanoTime() - startTime) / 1_000_000L);
                            }
                            return null;
                        } catch (Exception e) {
                            status.setRollbackOnly();
                            throw e;
                        }
                    });

                    latch.countDown();
                    return threadTimes;
                }));
            }

            // Wait for all threads to complete
            latch.await(30, TimeUnit.SECONDS);

            // Collect and analyze results
            List<Long> allTimes = futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .flatMap(List::stream)
                    .toList();

            DoubleSummaryStatistics stats = allTimes.stream()
                    .mapToDouble(Long::doubleValue)
                    .summaryStatistics();

            System.out.printf("""
                Concurrent Insert Performance:
                Total Records: %d
                Average time per record: %.2f ms
                Min time: %.2f ms
                Max time: %.2f ms
                Total time: %.2f ms%n""",
                allTimes.size(),
                stats.getAverage(),
                stats.getMin(),
                stats.getMax(),
                stats.getSum());

            // Count the number of users in the database
            long count = userRepository.count();
            assertEquals(threadsCount * recordsPerThread, count);
        } finally {
            executorService.shutdown();
        }
    }
}
