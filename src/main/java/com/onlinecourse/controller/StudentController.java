package com.onlinecourse.controller;

import com.onlinecourse.database.StudentDAO;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.AppException;
import com.onlinecourse.utils.ValidationUtils;

import java.util.ArrayList;

public class StudentController {
    private final StudentDAO studentDAO = new StudentDAO();

    public ArrayList<Student> getStudents() throws AppException {
        return studentDAO.findAll();
    }

    public ArrayList<Student> searchStudents(String keyword) throws AppException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getStudents();
        }
        return studentDAO.search(keyword.trim());
    }

    public void addStudent(String name, String email) throws AppException {
        studentDAO.add(new Student(
                ValidationUtils.requireText(name, "Name"),
                ValidationUtils.requireEmail(email)
        ));
    }

    public void updateStudent(int id, String name, String email) throws AppException {
        ensureSelected(id, "student");
        studentDAO.update(new Student(
                id,
                ValidationUtils.requireText(name, "Name"),
                ValidationUtils.requireEmail(email)
        ));
    }

    public void deleteStudent(int id) throws AppException {
        ensureSelected(id, "student");
        studentDAO.delete(id);
    }

    private void ensureSelected(int id, String itemName) throws AppException {
        if (id <= 0) {
            throw new AppException("Please select a " + itemName + " first.");
        }
    }
}
