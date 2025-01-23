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
public class TypeIdHibernateGeneratorTest {

    @Mock
    private SharedSessionContractImplementor session;

    private TypeIdHibernateGenerator generator;

    @BeforeEach
    public void setUp() {
        generator = new TypeIdHibernateGenerator();
    }

    @Test
    public void testPrefix() throws Exception {
        TestUserEntity entity = new TestUserEntity();
        Serializable result1 = generator.generate(session, entity);

        assertTrue(result1.toString().startsWith("u"), "The generated ID should start with the prefix 'u'.");
    }

    @Test
    public void testUnique() throws Exception {
        TestUserEntity entity = new TestUserEntity();

        Serializable result1 = generator.generate(session, entity);
        Serializable result2 = generator.generate(session, entity);
        Serializable result3 = generator.generate(session, entity);

        System.out.println(result1.toString());
        System.out.println(result2.toString());
        System.out.println(result3.toString());

        assertNotEquals(result1, result2, "The generated IDs should be different.");
        assertNotEquals(result1, result3, "The generated IDs should be different.");
        assertNotEquals(result2, result3, "The generated IDs should be different.");
    }
}
