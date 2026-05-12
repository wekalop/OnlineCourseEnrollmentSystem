package com.onlinecourse.controller;

import com.onlinecourse.database.CourseDAO;
import com.onlinecourse.database.EnrollmentDAO;
import com.onlinecourse.database.StudentDAO;
import com.onlinecourse.model.Course;
import com.onlinecourse.model.Enrollment;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.AppException;

import java.util.ArrayList;

public class EnrollmentController {
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    public ArrayList<Enrollment> getEnrollments() throws AppException {
        return enrollmentDAO.findAll();
    }

    public ArrayList<Student> getStudents() throws AppException {
        return studentDAO.findAll();
    }

    public ArrayList<Course> getCourses() throws AppException {
        return courseDAO.findAll();
    }

    public void enroll(int studentId, int courseId) throws AppException {
        validateSelection(studentId, "student");
        validateSelection(courseId, "course");
        enrollmentDAO.enroll(studentId, courseId);
    }

    public void unenroll(int studentId, int courseId) throws AppException {
        validateSelection(studentId, "student");
        validateSelection(courseId, "course");
        enrollmentDAO.unenroll(studentId, courseId);
    }

    private void validateSelection(int id, String name) throws AppException {
        if (id <= 0) {
            throw new AppException("Please select a " + name + ".");
        }
    }
}
