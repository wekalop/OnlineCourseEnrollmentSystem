package com.onlinecourse.database;

import com.onlinecourse.utils.AppException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class SqlScriptRunner {
    private SqlScriptRunner() {
    }

    static void runResource(Connection connection, String resourcePath) throws AppException {
        String script = readResource(resourcePath);
        runScript(connection, script);
    }

    private static String readResource(String resourcePath) throws AppException {
        try (InputStream inputStream = SqlScriptRunner.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new AppException("SQL resource not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new AppException("Could not read SQL resource: " + resourcePath, ex);
        }
    }

    private static void runScript(Connection connection, String script) throws AppException {
        try (Statement statement = connection.createStatement()) {
            for (String sql : stripLineComments(script).split(";")) {
                String command = sql.trim();
                if (!command.isEmpty()) {
                    statement.execute(command);
                }
            }
        } catch (SQLException ex) {
            throw new AppException("Database script failed: " + ex.getMessage(), ex);
        }
    }

    private static String stripLineComments(String script) {
        StringBuilder builder = new StringBuilder();
        for (String line : script.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("--")) {
                builder.append(line).append(System.lineSeparator());
            }
        }
        return builder.toString();
    }
}
