package com.github.landsman;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IdTypeIdTest {

    @Mock
    private SharedSessionContractImplementor session;

    private TypeIdGenerator generator;

    @BeforeEach
    public void setUp() {
        generator = new TypeIdGenerator();
    }

    @Test
    public void testIdTypeIdAnnotation() {
        // Create a test entity with @IdTypeId
        TestEntityWithIdTypeId entity = new TestEntityWithIdTypeId();
        
        // Generate an ID
        String generatedId = (String) generator.generate(session, entity);
        
        // Verify the ID starts with the expected prefix
        assertTrue(generatedId.startsWith("test_"), 
                   "The generated ID should start with the prefix 'test_'");
        
        // Verify the ID has the expected format
        assertTrue(generatedId.length() > 5, 
                   "The generated ID should have content after the prefix");
        
        System.out.println("[DEBUG_LOG] Generated ID: " + generatedId);
    }
}

class TestEntityWithIdTypeId {
    @IdTypeId(prefix = "test")
    private String id;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
}