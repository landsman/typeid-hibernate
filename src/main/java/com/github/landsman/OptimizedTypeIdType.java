package com.github.landsman;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Objects;

public class OptimizedTypeIdType implements UserType<TypeId>, EnhancedUserType<TypeId> {
    private String typePrefix;

    private String getTypePrefix(SharedSessionContractImplementor session, Object owner) {
        if (typePrefix != null) {
            return typePrefix;
        }

        if (owner != null) {
            Class<?> entityClass = owner.getClass();
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(IdTypeId.class)) {
                    IdTypeId annotation = field.getAnnotation(IdTypeId.class);
                    typePrefix = annotation.prefix() + "_";
                    return typePrefix;
                }
            }
        }
        throw new HibernateException("No @IdTypeId annotation found to determine type prefix");
    }

    @Override
    public Serializable disassemble(TypeId value) {
        return value == null ? null : value.getValue();
    }

    @Override
    public TypeId assemble(Serializable cached, Object owner) {
        return cached == null ? null : TypeId.of((String) cached);
    }

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<TypeId> returnedClass() {
        return TypeId.class;
    }

    @Override
    public boolean equals(TypeId x, TypeId y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(TypeId x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public TypeId nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        String value = rs.getString(position);
        return value == null ? null : TypeId.of(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, TypeId value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, value.getValue());
        }
    }
    
    @Override
    public TypeId deepCopy(TypeId value) {
        return value; // TypeId is immutable, so no need for deep copy
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    // todo: this can not be hardcoded.... it comes from IdTypeId
    private static final String TYPE_PREFIX = "type_";

    /**
     * Converts a TypeId to its SQL literal representation.
     *
     * @param value The TypeId to convert
     * @return SQL literal representation of the TypeId
     */
    @Override
    public String toSqlLiteral(TypeId value) {
        if (value == null) {
            return "null";
        }
        // Since we don't have access to the entity here, we can only store the raw value
        return "'" + value.getValue() + "'";
    }

    /**
     * Converts a TypeId to its string representation.
     *
     * @param value The TypeId to convert
     * @return String representation of the TypeId
     * @throws HibernateException if conversion fails
     */
    @Override
    public String toString(TypeId value) throws HibernateException {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    /**
     * Creates a TypeId from its string representation.
     *
     * @param sequence The string representation to convert
     * @return The converted TypeId
     * @throws HibernateException if conversion fails
     */
    @Override
    public TypeId fromStringValue(CharSequence sequence) throws HibernateException {
        if (sequence == null) {
            return null;
        }
        return TypeId.of(sequence.toString());
    }
}
