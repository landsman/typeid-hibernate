package com.github.landsman;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Combined annotation for TypeID fields.
 * 
 * This annotation is a convenience annotation that combines the functionality of:
 * - {@link jakarta.persistence.Id} (JPA primary key)
 * - {@link jakarta.persistence.Column}(unique = true) (database-level uniqueness)
 * - {@link org.hibernate.annotations.Type}(OptimizedTypeIdType.class) (Hibernate type)
 * - {@link TypeIdHibernate} (TypeID generation functionality)
 * 
 * Usage:
 * <pre>
 * {@code
 * @IdTypeId(prefix = "u")
 * private String id;
 * }
 * </pre>
 * 
 * Instead of:
 * <pre>
 * {@code
 * @Id
 * @Column(unique = true)
 * @Type(OptimizedTypeIdType.class)
 * @TypeIdHibernate(prefix = "u")
 * private String id;
 * }
 * </pre>
 */
@IdGeneratorType(TypeIdHibernateGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD })
@Documented
public @interface IdTypeId {

    /**
     * prefix of the entity, e.g. "u" for user, recommendation: max. 3 characters
     * @return the prefix for the generated id
     */
    String prefix();

    /**
     * Length of the random part of the ID (default: 10)
     * @return the length for the generated id
     */
    int length() default 10;
}