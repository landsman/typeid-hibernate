package com.github.landsman;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.annotations.Type;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Annotation for generating type ids.
 * 
 * This annotation combines the functionality of:
 * - @Id (JPA primary key)
 * - @Column(unique = true) (database-level uniqueness)
 * - @Type(OptimizedTypeIdType.class) (Hibernate type)
 * - TypeID generation functionality
 * 
 * Usage:
 * <pre>
 * {@code
 * @TypeIdHibernate(prefix = "u")
 * private String id;
 * }
 * </pre>
 * 
 * This will automatically:
 * 1. Mark the field as a primary key (@Id)
 * 2. Ensure database-level uniqueness (@Column(unique = true))
 * 3. Use the optimized type for storage (@Type(OptimizedTypeIdType.class))
 * 4. Generate a unique ID with the specified prefix
 * 
 * @deprecated Use {@link IdTypeId} instead, which provides the same functionality with a single annotation
 * and eliminates the need for additional annotations like {@code @Id}, {@code @Column}, and {@code @Type}.
 */
@Deprecated
@IdGeneratorType(TypeIdHibernateGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD })
@Documented
public @interface TypeIdHibernate {

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
