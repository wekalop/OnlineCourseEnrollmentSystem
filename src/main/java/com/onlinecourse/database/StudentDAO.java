package com.onlinecourse.database;

import com.onlinecourse.model.Student;
import com.onlinecourse.utils.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

public class StudentDAO {
    public ArrayList<Student> findAll() throws AppException {
        String sql = "SELECT id, name, email FROM students ORDER BY name";
        return queryStudents(sql);
    }

    public ArrayList<Student> search(String keyword) throws AppException {
        String sql = """
                SELECT id, name, email
                FROM students
                WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ?
                ORDER BY name
                """;
        ArrayList<Student> students = new ArrayList<>();
        String pattern = "%" + keyword.toLowerCase() + "%";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
            return students;
        } catch (SQLException ex) {
            throw new AppException("Could not search students.", ex);
        }
    }

    public void add(Student student) throws AppException {
        String sql = "INSERT INTO students(name, email) VALUES (?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    student.setId(keys.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw toStudentException("Could not add student.", ex);
        }
    }

    public Student register(Student student) throws AppException {
        String sql = "INSERT INTO students(name, email, password_hash, salt) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.setString(3, student.getPasswordHash());
            statement.setString(4, student.getSalt());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    student.setId(keys.getInt(1));
                }
            }
            return student;
        } catch (SQLException ex) {
            throw toStudentException("Could not register student.", ex);
        }
    }

    public Optional<Student> findByEmail(String email) throws AppException {
        String sql = "SELECT id, name, email, password_hash, salt FROM students WHERE LOWER(email) = LOWER(?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapStudentWithCredentials(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new AppException("Could not load student account.", ex);
        }
    }

    public Student activateLogin(Student student) throws AppException {
        String sql = "UPDATE students SET name = ?, password_hash = ?, salt = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getName());
            statement.setString(2, student.getPasswordHash());
            statement.setString(3, student.getSalt());
            statement.setInt(4, student.getId());
            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw new AppException("Student was not found.");
            }
            return student;
        } catch (SQLException ex) {
            throw new AppException("Could not activate student login.", ex);
        }
    }

    public void update(Student student) throws AppException {
        String sql = "UPDATE students SET name = ?, email = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.setInt(3, student.getId());
            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw new AppException("Student was not found.");
            }
        } catch (SQLException ex) {
            throw toStudentException("Could not update student.", ex);
        }
    }

    public void delete(int studentId) throws AppException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            int deleted = statement.executeUpdate();
            if (deleted == 0) {
                throw new AppException("Student was not found.");
            }
        } catch (SQLException ex) {
            throw new AppException("Could not delete student.", ex);
        }
    }

    private ArrayList<Student> queryStudents(String sql) throws AppException {
        ArrayList<Student> students = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(mapStudent(resultSet));
            }
            return students;
        } catch (SQLException ex) {
            throw new AppException("Could not load students.", ex);
        }
    }

    private Student mapStudent(ResultSet resultSet) throws SQLException {
        return new Student(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("email")
        );
    }

    private Student mapStudentWithCredentials(ResultSet resultSet) throws SQLException {
        return new Student(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("password_hash"),
                resultSet.getString("salt")
        );
    }

    private AppException toStudentException(String message, SQLException ex) {
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unique")) {
            return new AppException("A student with this email already exists.", ex);
        }
        return new AppException(message, ex);
    }
}
