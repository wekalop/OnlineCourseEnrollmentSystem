package com.onlinecourse.model;

public class Admin {
    private final int id;
    private final String username;
    private final String passwordHash;
    private final String salt;

    public Admin(int id, String username, String passwordHash, String salt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }
}
