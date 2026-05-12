package com.onlinecourse.model;

public class Admin extends User {

    public Admin(int id, String username, String passwordHash, String salt) {
        this.id = id;
        this.username = username;
        this.name = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    @Override
    public String getDisplayName() {
        return username;
    }
}
