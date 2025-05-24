package com.github.landsman;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Generator class for creating unique identifiers with a prefix.
 * This class implements the IdentifierGenerator interface to provide a custom identifier generation strategy for entities.
 */
public class TypeIdHibernateGenerator implements IdentifierGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Default constructor.
     * Initializes a new instance of the TypeIdHibernateGenerator.
     */
    public TypeIdHibernateGenerator() {
        super();
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        Class<?> entityClass = obj.getClass();
        TypeIdHibernate data = getDataFromAnnotation(entityClass);

        assert data != null;
        if (data.prefix() == null) {
            throw new HibernateException("No field annotated with @TypeIdHibernate found");
        }

        return generateRandomId(data.prefix(), data.length());
    }

    /**
     * Generates a random ID with the given prefix.
     * This method creates IDs that are significantly different from each other
     * at the character level.
     * 
     * @param prefix the prefix for the ID
     * @return a random ID with the given prefix
     */
    private String generateRandomId(String prefix, int length) {
        StringBuilder id = new StringBuilder(prefix).append("_");

        // Add a UUID-based component (highly random)
        String uuidPart = UUID.randomUUID().toString().replace("-", "");
        id.append(uuidPart, 0, Math.min(8, uuidPart.length()));

        // Add a random component with characters from the alphabet
        for (int i = 0; i < length - 8; i++) {
            id.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return id.toString();
    }

    /**
     * Get annotation data @TypeIdHibernate.
     * @param entityClass the entity class
     */
    private TypeIdHibernate getDataFromAnnotation(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(TypeIdHibernate.class)) {
                field.setAccessible(true);
                return field.getAnnotation(TypeIdHibernate.class);
            }
        }
        return null;
    }
}
