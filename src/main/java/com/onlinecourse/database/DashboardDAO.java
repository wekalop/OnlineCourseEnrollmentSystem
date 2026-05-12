package com.onlinecourse.database;

import com.onlinecourse.model.DashboardStats;
import com.onlinecourse.utils.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAO {
    public DashboardStats loadStats() throws AppException {
        try (Connection connection = DatabaseManager.getConnection()) {
            int totalStudents = count(connection, "students");
            int totalCourses = count(connection, "courses");
            int totalEnrollments = count(connection, "enrollments");
            Map<String, Integer> enrollmentsByCourse = enrollmentsByCourse(connection);
            String mostPopularCourse = mostPopularCourse(enrollmentsByCourse, totalEnrollments);
            return new DashboardStats(totalStudents, totalCourses, totalEnrollments, mostPopularCourse, enrollmentsByCourse);
        } catch (SQLException ex) {
            throw new AppException("Could not load dashboard statistics.", ex);
        }
    }

    private int count(Connection connection, String table) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    private Map<String, Integer> enrollmentsByCourse(Connection connection) throws SQLException {
        String sql = """
                SELECT c.name, COUNT(e.student_id) AS enrolled_count
                FROM courses c
                LEFT JOIN enrollments e ON c.id = e.course_id
                GROUP BY c.id, c.name
                ORDER BY enrolled_count DESC, c.name
                """;
        Map<String, Integer> values = new LinkedHashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                values.put(resultSet.getString("name"), resultSet.getInt("enrolled_count"));
            }
        }
        return values;
    }

    private String mostPopularCourse(Map<String, Integer> enrollmentsByCourse, int totalEnrollments) {
        if (totalEnrollments == 0 || enrollmentsByCourse.isEmpty()) {
            return "No enrollments yet";
        }
        return enrollmentsByCourse.entrySet().iterator().next().getKey();
    }
}
