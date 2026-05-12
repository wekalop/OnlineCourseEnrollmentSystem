package com.onlinecourse.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardStats {
    private final int totalStudents;
    private final int totalCourses;
    private final int totalEnrollments;
    private final String mostPopularCourse;
    private final Map<String, Integer> enrollmentsByCourse;

    public DashboardStats(
            int totalStudents,
            int totalCourses,
            int totalEnrollments,
            String mostPopularCourse,
            Map<String, Integer> enrollmentsByCourse
    ) {
        this.totalStudents = totalStudents;
        this.totalCourses = totalCourses;
        this.totalEnrollments = totalEnrollments;
        this.mostPopularCourse = mostPopularCourse;
        this.enrollmentsByCourse = new LinkedHashMap<>(enrollmentsByCourse);
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public int getTotalCourses() {
        return totalCourses;
    }

    public int getTotalEnrollments() {
        return totalEnrollments;
    }

    public String getMostPopularCourse() {
        return mostPopularCourse;
    }

    public Map<String, Integer> getEnrollmentsByCourse() {
        return new LinkedHashMap<>(enrollmentsByCourse);
    }
}
