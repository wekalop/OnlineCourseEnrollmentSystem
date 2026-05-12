package com.onlinecourse.controller;

import com.onlinecourse.database.CourseDAO;
import com.onlinecourse.database.DashboardDAO;
import com.onlinecourse.database.EnrollmentDAO;
import com.onlinecourse.database.StudentDAO;
import com.onlinecourse.model.Course;
import com.onlinecourse.model.Enrollment;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.AppException;
import com.onlinecourse.utils.CsvExporter;
import com.onlinecourse.utils.PdfExporter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReportController {
    private final StudentDAO studentDAO = new StudentDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public void exportStudentsCsv(Path file) throws AppException {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "Name", "Email"});
        for (Student student : studentDAO.findAll()) {
            rows.add(new String[]{String.valueOf(student.getId()), student.getName(), student.getEmail()});
        }
        CsvExporter.write(file, rows);
    }

    public void exportCoursesCsv(Path file) throws AppException {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "Name", "Instructor", "Schedule", "Capacity", "Enrolled", "Seats Left"});
        for (Course course : courseDAO.findAll()) {
            rows.add(new String[]{
                    String.valueOf(course.getId()),
                    course.getName(),
                    course.getInstructor(),
                    course.getScheduleText(),
                    String.valueOf(course.getCapacity()),
                    String.valueOf(course.getEnrolledCount()),
                    String.valueOf(course.getSeatsLeft())
            });
        }
        CsvExporter.write(file, rows);
    }

    public void exportEnrollmentsCsv(Path file) throws AppException {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Student ID", "Student", "Course ID", "Course", "Schedule", "Enrolled At"});
        for (Enrollment enrollment : enrollmentDAO.findAll()) {
            rows.add(new String[]{
                    String.valueOf(enrollment.getStudentId()),
                    enrollment.getStudentName(),
                    String.valueOf(enrollment.getCourseId()),
                    enrollment.getCourseName(),
                    enrollment.getScheduleText(),
                    enrollment.getEnrolledAt()
            });
        }
        CsvExporter.write(file, rows);
    }

    public void exportSummaryPdf(Path file) throws AppException {
        PdfExporter.writeSummaryReport(
                file,
                dashboardDAO.loadStats(),
                studentDAO.findAll(),
                courseDAO.findAll(),
                enrollmentDAO.findAll()
        );
    }
}
