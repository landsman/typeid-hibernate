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
 * 
 * Supports both {@link TypeIdHibernate} and {@link IdTypeId} annotations.
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

        // First try to get data from @IdTypeId annotation (preferred approach)
        IdTypeIdData idTypeIdData = getIdTypeIdData(entityClass);
        if (idTypeIdData != null) {
            return generateRandomId(idTypeIdData.prefix, idTypeIdData.length);
        }

        // Fall back to @TypeIdHibernate annotation (deprecated approach)
        TypeIdHibernate typeIdHibernate = getTypeIdHibernateData(entityClass);
        if (typeIdHibernate != null) {
            return generateRandomId(typeIdHibernate.prefix(), typeIdHibernate.length());
        }

        throw new HibernateException("No field annotated with @IdTypeId or @TypeIdHibernate found");
    }

    /**
     * Generates a random ID with the given prefix.
     * This method creates IDs that are significantly different from each other
     * at the character level.
     * 
     * @param prefix the prefix for the ID
     * @param length the length of the random part
     * @return a random ID with the given prefix
     */
    private String generateRandomId(String prefix, int length) {
        StringBuilder id = new StringBuilder(prefix).append("_");
        int uuidCharacters = 8;

        // Add a UUID-based component (highly random)
        String uuidPart = UUID.randomUUID().toString().replace("-", "");
        id.append(uuidPart, 0, Math.min(uuidCharacters, uuidPart.length()));

        // Add a random component with characters from the alphabet
        for (int i = 0; i < length - uuidCharacters; i++) {
            id.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return id.toString();
    }

    /**
     * Get data from @IdTypeId annotation.
     * @param entityClass the entity class
     * @return the IdTypeIdData or null if not found
     */
    private IdTypeIdData getIdTypeIdData(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(IdTypeId.class)) {
                field.setAccessible(true);
                IdTypeId annotation = field.getAnnotation(IdTypeId.class);
                return new IdTypeIdData(annotation.prefix(), annotation.length());
            }
        }
        return null;
    }

    /**
     * Get data from @TypeIdHibernate annotation.
     * @param entityClass the entity class
     * @return the TypeIdHibernate annotation or null if not found
     */
    private TypeIdHibernate getTypeIdHibernateData(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(TypeIdHibernate.class)) {
                field.setAccessible(true);
                return field.getAnnotation(TypeIdHibernate.class);
            }
        }
        return null;
    }

    /**
     * Simple data class to hold IdTypeId annotation data.
     */
    private static class IdTypeIdData {
        private final String prefix;
        private final int length;

        public IdTypeIdData(String prefix, int length) {
            this.prefix = prefix;
            this.length = length;
        }
    }
}
