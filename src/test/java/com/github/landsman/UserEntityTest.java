package com.github.landsman;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserEntityTest {

    @Mock
    private SharedSessionContractImplementor session;

    private TypeIdHibernateGenerator generator;

    @BeforeEach
    public void setUp() {
        generator = new TypeIdHibernateGenerator();
    }

    @Test
    public void testMissingEntityPrefix() {
        UserEntity entity = new UserEntity();
        Serializable result1 = generator.generate(session, entity);

        assertTrue(result1.toString().startsWith("u"), "The generated ID should start with the prefix 'u'.");
    }

    @Test
    public void testBunchOfUniqueIds() {
        UserEntity entity = new UserEntity();
        int howMany = 500;

        // Generate 10 IDs and print them immediately
        String[] generatedIds = new String[howMany];
        System.out.println("[DEBUG_LOG] Generating " + howMany + " IDs...");

        for (int i = 0; i < howMany; i++) {
            generatedIds[i] = generator.generate(session, entity).toString();
            System.out.println("[DEBUG_LOG] Generated ID " + (i+1) + ": " + generatedIds[i]);
        }

        // Verify all IDs have the expected format (prefix_base32string)
        for (String id : generatedIds) {
            assertTrue(id.startsWith("u_"), "ID should start with the prefix 'u_'");
            assertTrue(id.length() > 2, "ID should have content after the prefix");
        }

        // Verify all IDs are unique
        for (int i = 0; i < generatedIds.length; i++) {
            for (int j = i + 1; j < generatedIds.length; j++) {
                assertNotEquals(generatedIds[i], generatedIds[j], 
                    "The generated IDs should be different: " + generatedIds[i] + " vs " + generatedIds[j]);
            }
        }

        // Analyze the differences between IDs
        System.err.println("[DEBUG_LOG] Analyzing differences between IDs...");
        int totalDifferentPositions = 0;
        int comparisonCount = 0;

        for (int i = 0; i < generatedIds.length; i++) {
            for (int j = i + 1; j < generatedIds.length; j++) {
                // Find positions where the IDs differ
                int diffCount = getDiffCount(generatedIds, i, j);

                totalDifferentPositions += diffCount;
                comparisonCount++;

                // Assert that the IDs differ in at least one position (they are unique)
                assertTrue(diffCount > 0, "IDs should differ in at least one position");
            }
        }

        // Calculate the average length of IDs
        double avgLength = 0;
        for (String id : generatedIds) {
            avgLength += id.length();
        }
        avgLength /= generatedIds.length;

        // Calculate and report the average number of different positions
        double avgDifferentPositions = (double) totalDifferentPositions / comparisonCount;
        double percentageDifferent = (avgDifferentPositions / avgLength) * 100;

        System.err.println("[DEBUG_LOG] Average number of different positions between IDs (percentage): " + String.format("%.2f%%", percentageDifferent));

        // Assert that on average, IDs differ in multiple positions
        assertTrue(percentageDifferent >= 60,
                  "On average, IDs should differ in at least one position");
    }

    private static int getDiffCount(String[] generatedIds, int i, int j) {
        String id1 = generatedIds[i];
        String id2 = generatedIds[j];
        int minLength = Math.min(id1.length(), id2.length());

        int diffCount = 0;

        for (int pos = 0; pos < minLength; pos++) {
            if (id1.charAt(pos) != id2.charAt(pos)) {
                diffCount++;
            }
        }

        // Account for length differences
        if (id1.length() != id2.length()) {
            diffCount += Math.abs(id1.length() - id2.length());
        }
        return diffCount;
    }
}

class UserEntity {
    @TypeIdHibernate(prefix = "u", length = 24)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
