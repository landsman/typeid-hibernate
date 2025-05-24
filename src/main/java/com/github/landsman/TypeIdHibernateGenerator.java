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
    private static final int ID_LENGTH = 24; // Length of the random part of the ID

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
        String prefix = findPrefixFromAnnotation(entityClass);

        if (prefix == null) {
            throw new HibernateException("No field annotated with @TypeIdHibernate found");
        }

        // Generate a highly random ID with more character-level differences
        return generateRandomId(prefix);
    }

    /**
     * Generates a random ID with the given prefix.
     * This method creates IDs that are significantly different from each other
     * at the character level.
     * 
     * @param prefix the prefix for the ID
     * @return a random ID with the given prefix
     */
    private String generateRandomId(String prefix) {
        StringBuilder id = new StringBuilder(prefix).append("_");

        // Add a UUID-based component (highly random)
        String uuidPart = UUID.randomUUID().toString().replace("-", "");
        id.append(uuidPart, 0, Math.min(8, uuidPart.length()));

        // Add a random component with characters from the alphabet
        for (int i = 0; i < ID_LENGTH - 8; i++) {
            id.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return id.toString();
    }

    /**
     * Find the prefix from the annotation @TypeIdHibernate.
     * @param entityClass the entity class
     * @return the prefix
     */
    private String findPrefixFromAnnotation(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(TypeIdHibernate.class)) {
                field.setAccessible(true);
                TypeIdHibernate annotation = field.getAnnotation(TypeIdHibernate.class);
                return annotation.prefix();
            }
        }
        return null;
    }
}
