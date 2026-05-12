package com.onlinecourse.model;

import com.onlinecourse.utils.AppException;

import java.util.regex.Pattern;

public abstract class User {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    protected int id;
    protected String name;
    protected String email;
    protected String username;
    protected String passwordHash;
    protected String salt;
    protected String phone;

    protected User() {
    }

    protected User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    protected User(int id, String name, String email, String passwordHash, String salt) {
        this(id, name, email);
        this.passwordHash = passwordHash;
        this.salt = salt;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public abstract String getDisplayName();

    public String validateName() throws AppException {
        if (name == null || name.trim().isEmpty()) {
            throw new AppException("Name is required.");
        }
        return name.trim();
    }

    public String validateEmail() throws AppException {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new AppException("Please enter a valid email address.");
        }
        return email.toLowerCase();
    }
}
