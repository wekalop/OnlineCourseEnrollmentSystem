package com.onlinecourse.model;

public class Student extends User {

    public Student(int id, String name, String email) {
        super(id, name, email);
    }

    public Student(int id, String name, String email, String passwordHash, String salt) {
        super(id, name, email, passwordHash, salt);
    }

    public Student(String name, String email) {
        this(0, name, email);
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
