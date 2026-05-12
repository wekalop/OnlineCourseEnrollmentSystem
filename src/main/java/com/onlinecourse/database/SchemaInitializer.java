package com.onlinecourse.database;

import com.onlinecourse.utils.AppException;
import com.onlinecourse.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaInitializer {
    private SchemaInitializer() {
    }

    public static void initialize() throws AppException {
        try (Connection connection = DatabaseManager.getConnection()) {
            SqlScriptRunner.runResource(connection, "/sql/schema.sql");
            migrateStudentLoginColumns(connection);
            migrateCourseScheduleColumns(connection);
            seedDefaultAdmin(connection);
            seedSampleData(connection);
        } catch (SQLException ex) {
            throw new AppException("Could not initialize the database: " + ex.getMessage(), ex);
        }
    }

    private static void migrateStudentLoginColumns(Connection connection) throws SQLException {
        if (!columnExists(connection, "students", "password_hash")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE students ADD COLUMN password_hash TEXT");
            }
        }
        if (!columnExists(connection, "students", "salt")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE students ADD COLUMN salt TEXT");
            }
        }
    }

    private static boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, null, tableName, columnName)) {
            return columns.next();
        }
    }

    private static void migrateCourseScheduleColumns(Connection connection) throws SQLException {
        if (!columnExists(connection, "courses", "day_of_week")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE courses ADD COLUMN day_of_week TEXT NOT NULL DEFAULT 'Monday'");
            }
        }
        if (!columnExists(connection, "courses", "start_time")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE courses ADD COLUMN start_time TEXT NOT NULL DEFAULT '09:00'");
            }
        }
        if (!columnExists(connection, "courses", "end_time")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE courses ADD COLUMN end_time TEXT NOT NULL DEFAULT '10:30'");
            }
        }
    }

    private static void seedDefaultAdmin(Connection connection) throws SQLException {
        // The default account is hashed with a random salt, never stored as plain text.
        String existsSql = "SELECT COUNT(*) FROM admins WHERE username = ?";
        try (PreparedStatement exists = connection.prepareStatement(existsSql)) {
            exists.setString(1, "admin");
            try (ResultSet resultSet = exists.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return;
                }
            }
        }

        String salt = PasswordUtils.newSalt();
        String passwordHash = PasswordUtils.hashPassword("admin123".toCharArray(), salt);
        String insertSql = "INSERT INTO admins(username, password_hash, salt) VALUES (?, ?, ?)";
        try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
            insert.setString(1, "admin");
            insert.setString(2, passwordHash);
            insert.setString(3, salt);
            insert.executeUpdate();
        }
    }

    private static void seedSampleData(Connection connection) throws AppException, SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next() && resultSet.getInt(1) == 0) {
                SqlScriptRunner.runResource(connection, "/sql/sample_data.sql");
            }
        }
    }
}
