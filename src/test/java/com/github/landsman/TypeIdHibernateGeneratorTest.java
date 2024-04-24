package com.github.landsman;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void testGenerate() throws Exception {
        TestUserEntity entity = new TestUserEntity();
        Serializable generatedId = generator.generate(session, entity);

        assertTrue(generatedId.toString().startsWith("u"), "The generated ID should start with the prefix 'u'.");
    }
}
