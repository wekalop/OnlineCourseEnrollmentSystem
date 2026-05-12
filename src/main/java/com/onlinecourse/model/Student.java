package com.onlinecourse.model;

public class Student {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String salt;

    public Student(int id, String name, String email) {
        this(id, name, email, null, null);
    }

    public Student(int id, String name, String email, String passwordHash, String salt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public Student(String name, String email) {
        this(0, name, email);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
