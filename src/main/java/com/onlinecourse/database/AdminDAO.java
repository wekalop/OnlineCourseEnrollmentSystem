package com.onlinecourse.database;

import com.onlinecourse.model.Admin;
import com.onlinecourse.utils.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AdminDAO {
    public Optional<Admin> findByUsername(String username) throws AppException {
        String sql = "SELECT id, username, password_hash, salt FROM admins WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Admin(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password_hash"),
                            resultSet.getString("salt")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new AppException("Could not load admin account.", ex);
        }
    }
}
