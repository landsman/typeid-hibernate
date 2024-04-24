package com.github.landsman;

public class TestUserEntity {
    @TypeIdHibernate(prefix = "u")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
