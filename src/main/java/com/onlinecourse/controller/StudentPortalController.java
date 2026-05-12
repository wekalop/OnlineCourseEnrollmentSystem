package com.onlinecourse.controller;

import com.onlinecourse.database.CourseDAO;
import com.onlinecourse.database.EnrollmentDAO;
import com.onlinecourse.model.Course;
import com.onlinecourse.model.Enrollment;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.AppException;

import java.util.ArrayList;

public class StudentPortalController {
    private final CourseDAO courseDAO = new CourseDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final Student student;

    public StudentPortalController(Student student) {
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    public ArrayList<Course> getAvailableCourses() throws AppException {
        return courseDAO.findAll();
    }

    public ArrayList<Enrollment> getMyEnrollments() throws AppException {
        return enrollmentDAO.findByStudent(student.getId());
    }

    public void enroll(int courseId) throws AppException {
        if (courseId <= 0) {
            throw new AppException("Please select a course.");
        }
        enrollmentDAO.enroll(student.getId(), courseId);
    }

    public void unenroll(int courseId) throws AppException {
        if (courseId <= 0) {
            throw new AppException("Please select an enrollment.");
        }
        enrollmentDAO.unenroll(student.getId(), courseId);
    }
}
