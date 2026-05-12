package com.onlinecourse.model;

public class Instructor extends User {

    public Instructor(int id, String name) {
        this.id = id;
        this.name = name;
        this.username = name;
    }

    public Instructor(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.username = name;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
