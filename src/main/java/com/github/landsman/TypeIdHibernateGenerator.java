package com.github.landsman;

import de.fxlae.typeid.TypeId;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Generator class for creating unique identifiers with a prefix.
 * This class implements the IdentifierGenerator interface to provide
 * custom identifier generation strategy for entities.
 */
public class TypeIdHibernateGenerator implements IdentifierGenerator {

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

        return TypeId.generate(prefix).toString();
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
