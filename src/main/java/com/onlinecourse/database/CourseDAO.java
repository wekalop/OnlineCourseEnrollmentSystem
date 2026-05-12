package com.onlinecourse.database;

import com.onlinecourse.model.Course;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CourseDAO {
    private static final String DAY_ORDER = """
            CASE c.day_of_week
                WHEN 'Monday' THEN 1
                WHEN 'Tuesday' THEN 2
                WHEN 'Wednesday' THEN 3
                WHEN 'Thursday' THEN 4
                WHEN 'Friday' THEN 5
                WHEN 'Saturday' THEN 6
                WHEN 'Sunday' THEN 7
                ELSE 8
            END
            """;
    private static final String COURSE_SELECT = """
            SELECT c.id, c.name, c.instructor, c.capacity, c.day_of_week, c.start_time, c.end_time,
                   COUNT(e.student_id) AS enrolled_count
            FROM courses c
            LEFT JOIN enrollments e ON c.id = e.course_id
            """;

    public ArrayList<Course> findAll() throws AppException {
        String sql = COURSE_SELECT
                + " GROUP BY c.id, c.name, c.instructor, c.capacity, c.day_of_week, c.start_time, c.end_time "
                + " ORDER BY " + DAY_ORDER + ", c.start_time, c.name";
        return queryCourses(sql);
    }

    public ArrayList<Course> search(String keyword) throws AppException {
        String sql = COURSE_SELECT
                + " WHERE LOWER(c.name) LIKE ? OR LOWER(c.instructor) LIKE ? OR LOWER(c.day_of_week) LIKE ? "
                + " GROUP BY c.id, c.name, c.instructor, c.capacity, c.day_of_week, c.start_time, c.end_time "
                + " ORDER BY " + DAY_ORDER + ", c.start_time, c.name";
        ArrayList<Course> courses = new ArrayList<>();
        String pattern = "%" + keyword.toLowerCase() + "%";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setString(3, pattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courses.add(mapCourse(resultSet));
                }
            }
            return courses;
        } catch (SQLException ex) {
            throw new AppException("Could not search courses.", ex);
        }
    }

    public void add(Course course) throws AppException {
        String sql = "INSERT INTO courses(name, instructor, capacity, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ensureInstructorAvailable(connection, course, 0);
            statement.setString(1, course.getName());
            statement.setString(2, course.getInstructor());
            statement.setInt(3, course.getCapacity());
            statement.setString(4, course.getDayOfWeek());
            statement.setString(5, course.getStartTime());
            statement.setString(6, course.getEndTime());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    course.setId(keys.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new AppException("Could not add course.", ex);
        }
    }

    public void update(Course course) throws AppException {
        String sql = "UPDATE courses SET name = ?, instructor = ?, capacity = ?, day_of_week = ?, start_time = ?, end_time = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection()) {
            // Do not allow an admin to shrink capacity below the number already enrolled.
            int currentEnrollment = getEnrollmentCount(connection, course.getId());
            if (course.getCapacity() < currentEnrollment) {
                throw new AppException("Capacity cannot be lower than the current enrolled student count (" + currentEnrollment + ").");
            }
            ensureInstructorAvailable(connection, course, course.getId());
            ensureUpdateDoesNotCreateStudentConflicts(connection, course);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, course.getName());
                statement.setString(2, course.getInstructor());
                statement.setInt(3, course.getCapacity());
                statement.setString(4, course.getDayOfWeek());
                statement.setString(5, course.getStartTime());
                statement.setString(6, course.getEndTime());
                statement.setInt(7, course.getId());
                int updated = statement.executeUpdate();
                if (updated == 0) {
                    throw new AppException("Course was not found.");
                }
            }
        } catch (SQLException ex) {
            throw new AppException("Could not update course.", ex);
        }
    }

    public void delete(int courseId) throws AppException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            int deleted = statement.executeUpdate();
            if (deleted == 0) {
                throw new AppException("Course was not found.");
            }
        } catch (SQLException ex) {
            throw new AppException("Could not delete course.", ex);
        }
    }

    public ArrayList<Student> findEnrolledStudents(int courseId) throws AppException {
        String sql = """
                SELECT s.id, s.name, s.email
                FROM enrollments e
                JOIN students s ON s.id = e.student_id
                WHERE e.course_id = ?
                ORDER BY s.name
                """;
        ArrayList<Student> students = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(new Student(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email")
                    ));
                }
            }
            return students;
        } catch (SQLException ex) {
            throw new AppException("Could not load enrolled students.", ex);
        }
    }

    private ArrayList<Course> queryCourses(String sql) throws AppException {
        ArrayList<Course> courses = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                courses.add(mapCourse(resultSet));
            }
            return courses;
        } catch (SQLException ex) {
            throw new AppException("Could not load courses.", ex);
        }
    }

    private int getEnrollmentCount(Connection connection, int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }

    private void ensureUpdateDoesNotCreateStudentConflicts(Connection connection, Course course) throws SQLException, AppException {
        String sql = """
                SELECT s.name AS student_name, other.name AS conflict_course
                FROM enrollments target_enrollment
                JOIN students s ON s.id = target_enrollment.student_id
                JOIN enrollments other_enrollment ON other_enrollment.student_id = target_enrollment.student_id
                JOIN courses other ON other.id = other_enrollment.course_id
                WHERE target_enrollment.course_id = ?
                  AND other.id <> ?
                  AND other.day_of_week = ?
                  AND ? < other.end_time
                  AND ? > other.start_time
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, course.getId());
            statement.setInt(2, course.getId());
            statement.setString(3, course.getDayOfWeek());
            statement.setString(4, course.getStartTime());
            statement.setString(5, course.getEndTime());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    throw new AppException("This timing conflicts with " + resultSet.getString("student_name")
                            + "'s existing course: " + resultSet.getString("conflict_course") + ".");
                }
            }
        }
    }

    private void ensureInstructorAvailable(Connection connection, Course course, int excludedCourseId) throws SQLException, AppException {
        String sql = """
                SELECT name
                FROM courses
                WHERE LOWER(instructor) = LOWER(?)
                  AND id <> ?
                  AND day_of_week = ?
                  AND ? < end_time
                  AND ? > start_time
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, course.getInstructor());
            statement.setInt(2, excludedCourseId);
            statement.setString(3, course.getDayOfWeek());
            statement.setString(4, course.getStartTime());
            statement.setString(5, course.getEndTime());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    throw new AppException("Instructor schedule conflict with course: " + resultSet.getString("name") + ".");
                }
            }
        }
    }

    private Course mapCourse(ResultSet resultSet) throws SQLException {
        return new Course(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("instructor"),
                resultSet.getInt("capacity"),
                resultSet.getInt("enrolled_count"),
                resultSet.getString("day_of_week"),
                resultSet.getString("start_time"),
                resultSet.getString("end_time")
        );
    }
}
