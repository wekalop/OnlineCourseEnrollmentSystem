package com.onlinecourse.database;

import com.onlinecourse.model.Enrollment;
import com.onlinecourse.utils.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EnrollmentDAO {
    public ArrayList<Enrollment> findAll() throws AppException {
        String sql = """
                SELECT e.student_id, e.course_id, s.name AS student_name, c.name AS course_name,
                       c.day_of_week, c.start_time, c.end_time, e.enrolled_at
                FROM enrollments e
                JOIN students s ON s.id = e.student_id
                JOIN courses c ON c.id = e.course_id
                ORDER BY e.enrolled_at DESC, s.name
                """;
        ArrayList<Enrollment> enrollments = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                enrollments.add(mapEnrollment(resultSet));
            }
            return enrollments;
        } catch (SQLException ex) {
            throw new AppException("Could not load enrollments.", ex);
        }
    }

    public ArrayList<Enrollment> findByStudent(int studentId) throws AppException {
        String sql = """
                SELECT e.student_id, e.course_id, s.name AS student_name, c.name AS course_name,
                       c.day_of_week, c.start_time, c.end_time, e.enrolled_at
                FROM enrollments e
                JOIN students s ON s.id = e.student_id
                JOIN courses c ON c.id = e.course_id
                WHERE e.student_id = ?
                ORDER BY
                    CASE c.day_of_week
                        WHEN 'Monday' THEN 1
                        WHEN 'Tuesday' THEN 2
                        WHEN 'Wednesday' THEN 3
                        WHEN 'Thursday' THEN 4
                        WHEN 'Friday' THEN 5
                        WHEN 'Saturday' THEN 6
                        WHEN 'Sunday' THEN 7
                        ELSE 8
                    END,
                    c.start_time,
                    c.name
                """;
        ArrayList<Enrollment> enrollments = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    enrollments.add(mapEnrollment(resultSet));
                }
            }
            return enrollments;
        } catch (SQLException ex) {
            throw new AppException("Could not load student enrollments.", ex);
        }
    }

    public void enroll(int studentId, int courseId) throws AppException {
        try (Connection connection = DatabaseManager.getConnection()) {
            // The checks and insert run in one transaction so capacity rules stay consistent.
            connection.setAutoCommit(false);
            try {
                ensureNoDuplicate(connection, studentId, courseId);
                ensureCourseHasSeat(connection, courseId);
                ensureNoScheduleConflict(connection, studentId, courseId);
                insertEnrollment(connection, studentId, courseId);
                connection.commit();
            } catch (AppException | SQLException ex) {
                connection.rollback();
                if (ex instanceof AppException appException) {
                    throw appException;
                }
                throw new AppException("Could not enroll student.", ex);
            }
        } catch (SQLException ex) {
            throw new AppException("Could not enroll student.", ex);
        }
    }

    public void unenroll(int studentId, int courseId) throws AppException {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            int deleted = statement.executeUpdate();
            if (deleted == 0) {
                throw new AppException("Enrollment was not found.");
            }
        } catch (SQLException ex) {
            throw new AppException("Could not unenroll student.", ex);
        }
    }

    private void ensureNoDuplicate(Connection connection, int studentId, int courseId) throws SQLException, AppException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    throw new AppException("This student is already enrolled in the selected course.");
                }
            }
        }
    }

    private void ensureCourseHasSeat(Connection connection, int courseId) throws SQLException, AppException {
        String sql = """
                SELECT c.capacity, COUNT(e.student_id) AS enrolled_count
                FROM courses c
                LEFT JOIN enrollments e ON c.id = e.course_id
                WHERE c.id = ?
                GROUP BY c.id, c.capacity
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new AppException("Course was not found.");
                }
                int capacity = resultSet.getInt("capacity");
                int enrolledCount = resultSet.getInt("enrolled_count");
                if (enrolledCount >= capacity) {
                    throw new AppException("This course is full.");
                }
            }
        }
    }

    private void ensureNoScheduleConflict(Connection connection, int studentId, int courseId) throws SQLException, AppException {
        String sql = """
                SELECT existing.name AS conflict_course,
                       existing.day_of_week, existing.start_time, existing.end_time
                FROM courses requested
                JOIN enrollments enrollment ON enrollment.student_id = ?
                JOIN courses existing ON existing.id = enrollment.course_id
                WHERE requested.id = ?
                  AND existing.day_of_week = requested.day_of_week
                  AND requested.start_time < existing.end_time
                  AND requested.end_time > existing.start_time
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    throw new AppException("Schedule conflict with " + resultSet.getString("conflict_course")
                            + " (" + resultSet.getString("day_of_week") + " "
                            + resultSet.getString("start_time") + "-" + resultSet.getString("end_time") + ").");
                }
            }
        }
    }

    private void insertEnrollment(Connection connection, int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO enrollments(student_id, course_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            statement.executeUpdate();
        }
    }

    private Enrollment mapEnrollment(ResultSet resultSet) throws SQLException {
        return new Enrollment(
                resultSet.getInt("student_id"),
                resultSet.getInt("course_id"),
                resultSet.getString("student_name"),
                resultSet.getString("course_name"),
                resultSet.getString("enrolled_at"),
                resultSet.getString("day_of_week"),
                resultSet.getString("start_time"),
                resultSet.getString("end_time")
        );
    }
}
