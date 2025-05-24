package com.github.landsman;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Annotation for generating type ids.
 */
@IdGeneratorType( TypeIdHibernateGenerator.class )
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD })
public @interface TypeIdHibernate {

    /**
     * prefix of the entity, e.g. "u" for user, recommendation: max. 3 characters
     * @return the prefix for the generated id
     */
    String prefix();

    int length() default 10;
}
