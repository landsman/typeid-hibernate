package com.github.landsman.user;

import com.github.landsman.TestApplication;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserPerformanceTest {

    @PersistenceContext
    private EntityManager entityManager;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void testBulkInsertPerformance() {
        int batchSize = 50;
        int totalUsers = 200; // Reduced for faster test execution
        List<Long> batchTimes = new ArrayList<>();

        // Warm up
        for (int i = 0; i < 10; i++) {
            User user = new User();
            entityManager.persist(user);
        }
        entityManager.flush();
        entityManager.clear();

        // Test batched inserts
        for (int batch = 0; batch < totalUsers/batchSize; batch++) {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < batchSize; i++) {
                users.add(new User());
            }

            long startTime = System.nanoTime();
            for (User user : users) {
                entityManager.persist(user);
            }
            entityManager.flush();
            entityManager.clear();
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
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();
        assertEquals(totalUsers, count);
    }

    @Test
    public void testQueryPerformance() {
        // Prepare test data
        int testDataSize = 100; // Reduced for faster test execution
        List<User> users = new ArrayList<>();
        for (int i = 0; i < testDataSize; i++) {
            User user = new User();
            entityManager.persist(user);
            users.add(user);
        }
        entityManager.flush();
        entityManager.clear();

        // Test different query patterns
        Map<String, Long> queryTimes = new HashMap<>();

        // Test 1: Find by ID
        String randomId = users.get(new Random().nextInt(users.size())).getId();
        long startTime = System.nanoTime();
        entityManager.find(User.class, randomId);
        queryTimes.put("Find by ID", (System.nanoTime() - startTime) / 1_000_000L);

        // Test 2: Find all (with limit)
        startTime = System.nanoTime();
        entityManager.createQuery("SELECT u FROM User u", User.class)
                .setMaxResults(100)
                .getResultList();
        queryTimes.put("Find all (limit 100)", (System.nanoTime() - startTime) / 1_000_000L);

        // Test 3: ID prefix search
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
        int threadsCount = 2; // Reduced for faster test execution
        int recordsPerThread = 25; // Reduced for faster test execution
        CountDownLatch latch = new CountDownLatch(threadsCount);
        List<Future<List<Long>>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);

        try {
            // Create and submit tasks
            for (int i = 0; i < threadsCount; i++) {
                futures.add(executorService.submit(() -> {
                    List<Long> threadTimes = new ArrayList<>();
                    // Create a new EntityManager for each thread
                    EntityManager threadEntityManager = entityManagerFactory.createEntityManager();
                    try {
                        threadEntityManager.getTransaction().begin();
                        try {
                            for (int j = 0; j < recordsPerThread; j++) {
                                long startTime = System.nanoTime();
                                User user = new User();
                                threadEntityManager.persist(user);
                                threadEntityManager.flush();
                                threadTimes.add((System.nanoTime() - startTime) / 1_000_000L);
                            }
                            threadEntityManager.getTransaction().commit();
                        } catch (Exception e) {
                            if (threadEntityManager.getTransaction().isActive()) {
                                threadEntityManager.getTransaction().rollback();
                            }
                            throw e;
                        }
                    } finally {
                        threadEntityManager.close();
                    }
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
                    .collect(Collectors.toList());

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
            Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                    .getSingleResult();
            assertEquals(threadsCount * recordsPerThread, count);
        } finally {
            executorService.shutdown();
        }
    }
}
