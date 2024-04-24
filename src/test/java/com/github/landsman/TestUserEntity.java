package com.github.landsman;

public class TestUserEntity {
    @TypeIdHibernate(entityPrefix = "u")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
