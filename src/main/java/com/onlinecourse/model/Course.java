package com.onlinecourse.model;

public class Course {
    private int id;
    private String name;
    private String instructor;
    private int capacity;
    private int enrolledCount;
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    public Course(int id, String name, String instructor, int capacity, int enrolledCount) {
        this(id, name, instructor, capacity, enrolledCount, "Monday", "09:00", "10:30");
    }

    public Course(int id, String name, String instructor, int capacity, int enrolledCount, String dayOfWeek, String startTime, String endTime) {
        this.id = id;
        this.name = name;
        this.instructor = instructor;
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Course(String name, String instructor, int capacity) {
        this(0, name, instructor, capacity, 0);
    }

    public Course(String name, String instructor, int capacity, String dayOfWeek, String startTime, String endTime) {
        this(0, name, instructor, capacity, 0, dayOfWeek, startTime, endTime);
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

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(int enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public int getSeatsLeft() {
        return Math.max(0, capacity - enrolledCount);
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getScheduleText() {
        return dayOfWeek + " " + startTime + "-" + endTime;
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + getScheduleText() + ", " + getSeatsLeft() + " seats)";
    }
}
