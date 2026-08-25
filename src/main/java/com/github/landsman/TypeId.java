package com.github.landsman;

import java.io.Serializable;
import java.util.Objects;

public class TypeId implements Serializable {
    private final String value;

    private TypeId(String value) {
        this.value = value;
    }

    public static TypeId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("TypeId value cannot be null");
        }
        return new TypeId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeId typeId = (TypeId) o;
        return Objects.equals(value, typeId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
