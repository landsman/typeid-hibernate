package com.github.landsman;

import de.fxlae.typeid.TypeId;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;
import java.lang.reflect.Field;

public class TypeIdHibernateGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        Class<?> entityClass = obj.getClass();
        String prefix = null;

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(TypeIdHibernate.class)) {
                TypeIdHibernate annotation = field.getAnnotation(TypeIdHibernate.class);
                prefix = annotation.prefix();
                break;
            }
        }

        if (prefix == null) {
            throw new HibernateException("No field annotated with @TypeIdHibernate found");
        }

        return String.valueOf(TypeId.generate(prefix));
    }
}
