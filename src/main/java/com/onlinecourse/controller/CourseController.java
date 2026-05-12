package com.onlinecourse.controller;

import com.onlinecourse.database.CourseDAO;
import com.onlinecourse.model.Course;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.AppException;
import com.onlinecourse.utils.ValidationUtils;

import java.util.ArrayList;

public class CourseController {
    private final CourseDAO courseDAO = new CourseDAO();

    public ArrayList<Course> getCourses() throws AppException {
        return courseDAO.findAll();
    }

    public ArrayList<Course> searchCourses(String keyword) throws AppException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getCourses();
        }
        return courseDAO.search(keyword.trim());
    }

    public void addCourse(String name, String instructor, String capacity, String dayOfWeek, String startTime, String endTime) throws AppException {
        String cleanStart = ValidationUtils.requireTime(startTime, "Start time");
        String cleanEnd = ValidationUtils.requireTime(endTime, "End time");
        ValidationUtils.requireTimeRange(cleanStart, cleanEnd);
        courseDAO.add(new Course(
                ValidationUtils.requireText(name, "Course name"),
                ValidationUtils.requireText(instructor, "Instructor"),
                ValidationUtils.requirePositiveInt(capacity, "Capacity"),
                ValidationUtils.requireDay(dayOfWeek),
                cleanStart,
                cleanEnd
        ));
    }

    public void updateCourse(int id, String name, String instructor, String capacity, String dayOfWeek, String startTime, String endTime) throws AppException {
        ensureSelected(id, "course");
        String cleanStart = ValidationUtils.requireTime(startTime, "Start time");
        String cleanEnd = ValidationUtils.requireTime(endTime, "End time");
        ValidationUtils.requireTimeRange(cleanStart, cleanEnd);
        courseDAO.update(new Course(
                id,
                ValidationUtils.requireText(name, "Course name"),
                ValidationUtils.requireText(instructor, "Instructor"),
                ValidationUtils.requirePositiveInt(capacity, "Capacity"),
                0,
                ValidationUtils.requireDay(dayOfWeek),
                cleanStart,
                cleanEnd
        ));
    }

    public void deleteCourse(int id) throws AppException {
        ensureSelected(id, "course");
        courseDAO.delete(id);
    }

    public ArrayList<Student> getEnrolledStudents(int courseId) throws AppException {
        ensureSelected(courseId, "course");
        return courseDAO.findEnrolledStudents(courseId);
    }

    private void ensureSelected(int id, String itemName) throws AppException {
        if (id <= 0) {
            throw new AppException("Please select a " + itemName + " first.");
        }
    }
}
