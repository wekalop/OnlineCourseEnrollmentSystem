package com.onlinecourse.database;

import com.onlinecourse.utils.AppException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {
    private static final String DEFAULT_DB_PATH = "data/course_enrollment.db";

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("SQLite JDBC driver is missing from the classpath.", ex);
        }

        Path dbPath = getDatabasePath();
        Path parent = dbPath.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException ex) {
                throw new SQLException("Could not create database directory: " + parent, ex);
            }
        }

        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        try (Statement statement = connection.createStatement()) {
            // SQLite requires this pragma per connection for cascading deletes and FK checks.
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void initializeDatabase() throws AppException {
        SchemaInitializer.initialize();
    }

    public static Path getDatabasePath() {
        return Paths.get(System.getProperty("course.db.path", DEFAULT_DB_PATH));
    }
}
