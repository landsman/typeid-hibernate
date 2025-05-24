package com.github.landsman;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CustomEntityTest {

    @Mock
    private SharedSessionContractImplementor session;

    private TypeIdHibernateGenerator generator;

    @BeforeEach
    public void setUp() {
        generator = new TypeIdHibernateGenerator();
    }

    @Test
    public void testCustomEntityIdGeneration() {
        // Create a test entity
        CustomEntity entity = new CustomEntity();
        
        // Generate an ID
        String generatedId = (String) generator.generate(session, entity);
        
        // Verify the ID starts with the expected prefix
        assertTrue(generatedId.startsWith("custom"), 
                   "The generated ID should start with the prefix 'custom'");
        
        // Print the generated ID for debugging
        System.out.println("[DEBUG_LOG] Generated ID: " + generatedId);
    }
}

// Example test entity
class CustomEntity {
    @TypeIdHibernate(prefix = "custom")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}