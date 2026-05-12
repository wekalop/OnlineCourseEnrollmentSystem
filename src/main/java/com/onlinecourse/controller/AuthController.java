package com.onlinecourse.controller;

import com.onlinecourse.database.AdminDAO;
import com.onlinecourse.database.StudentDAO;
import com.onlinecourse.model.Admin;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.AppException;
import com.onlinecourse.utils.PasswordUtils;
import com.onlinecourse.utils.ValidationUtils;

import java.util.Arrays;
import java.util.Optional;

public class AuthController {
    private final AdminDAO adminDAO = new AdminDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public boolean login(String username, char[] password) throws AppException {
        String cleanUsername = ValidationUtils.requireText(username, "Username");
        if (password == null || password.length == 0) {
            throw new AppException("Password is required.");
        }

        try {
            Admin admin = adminDAO.findByUsername(cleanUsername)
                    .orElseThrow(() -> new AppException("Invalid username or password."));
            String submittedHash = PasswordUtils.hashPassword(password, admin.getSalt());
            if (!submittedHash.equals(admin.getPasswordHash())) {
                throw new AppException("Invalid username or password.");
            }
            return true;
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
        }
    }

    public Student loginStudent(String email, char[] password) throws AppException {
        String cleanEmail = ValidationUtils.requireEmail(email);
        if (password == null || password.length == 0) {
            throw new AppException("Password is required.");
        }

        try {
            Student student = studentDAO.findByEmail(cleanEmail)
                    .orElseThrow(() -> new AppException("Invalid email or password."));
            if (student.getPasswordHash() == null || student.getSalt() == null) {
                throw new AppException("This student was added by an admin and does not have a login account yet. Please register with a different email or ask the admin to remove the old record.");
            }
            String submittedHash = PasswordUtils.hashPassword(password, student.getSalt());
            if (!submittedHash.equals(student.getPasswordHash())) {
                throw new AppException("Invalid email or password.");
            }
            return student;
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
        }
    }

    public Student registerStudent(String name, String email, char[] password, char[] confirmPassword) throws AppException {
        String cleanName = ValidationUtils.requireText(name, "Name");
        String cleanEmail = ValidationUtils.requireEmail(email);
        if (password == null || password.length < 6) {
            throw new AppException("Password must be at least 6 characters.");
        }
        if (!Arrays.equals(password, confirmPassword)) {
            throw new AppException("Passwords do not match.");
        }

        try {
            String salt = PasswordUtils.newSalt();
            String passwordHash = PasswordUtils.hashPassword(password, salt);
            Optional<Student> existingStudent = studentDAO.findByEmail(cleanEmail);
            if (existingStudent.isPresent()) {
                Student student = existingStudent.get();
                if (student.getPasswordHash() != null && student.getSalt() != null) {
                    throw new AppException("A student account with this email already exists.");
                }
                student.setName(cleanName);
                student.setPasswordHash(passwordHash);
                student.setSalt(salt);
                return studentDAO.activateLogin(student);
            }
            return studentDAO.register(new Student(0, cleanName, cleanEmail, passwordHash, salt));
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
            if (confirmPassword != null) {
                Arrays.fill(confirmPassword, '\0');
            }
        }
    }
}
