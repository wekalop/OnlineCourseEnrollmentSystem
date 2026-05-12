package com.onlinecourse.model;

public class Enrollment {
    private final int studentId;
    private final int courseId;
    private final String studentName;
    private final String courseName;
    private final String enrolledAt;
    private final String dayOfWeek;
    private final String startTime;
    private final String endTime;

    public Enrollment(int studentId, int courseId, String studentName, String courseName, String enrolledAt) {
        this(studentId, courseId, studentName, courseName, enrolledAt, "", "", "");
    }

    public Enrollment(int studentId, int courseId, String studentName, String courseName, String enrolledAt, String dayOfWeek, String startTime, String endTime) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.studentName = studentName;
        this.courseName = courseName;
        this.enrolledAt = enrolledAt;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getEnrolledAt() {
        return enrolledAt;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getScheduleText() {
        if (dayOfWeek == null || dayOfWeek.isBlank()) {
            return "";
        }
        return dayOfWeek + " " + startTime + "-" + endTime;
    }
}
