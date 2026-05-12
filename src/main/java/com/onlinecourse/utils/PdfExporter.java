package com.onlinecourse.utils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.onlinecourse.model.Course;
import com.onlinecourse.model.DashboardStats;
import com.onlinecourse.model.Enrollment;
import com.onlinecourse.model.Student;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class PdfExporter {
    private PdfExporter() {
    }

    public static void writeSummaryReport(
            Path file,
            DashboardStats stats,
            List<Student> students,
            List<Course> courses,
            List<Enrollment> enrollments
    ) throws AppException {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (OutputStream outputStream = Files.newOutputStream(file)) {
                Document document = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(document, outputStream);
                document.open();
                addTitle(document);
                addStats(document, stats);
                addStudentTable(document, students);
                addCourseTable(document, courses);
                addEnrollmentTable(document, enrollments);
                document.close();
            }
        } catch (DocumentException | IOException ex) {
            throw new AppException("Could not export PDF report.", ex);
        }
    }

    private static void addTitle(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Paragraph title = new Paragraph("Online Course Enrollment System Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(18f);
        document.add(title);
    }

    private static void addStats(Document document, DashboardStats stats) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        addHeader(table, "Students");
        addHeader(table, "Courses");
        addHeader(table, "Enrollments");
        addHeader(table, "Most Popular Course");
        table.addCell(String.valueOf(stats.getTotalStudents()));
        table.addCell(String.valueOf(stats.getTotalCourses()));
        table.addCell(String.valueOf(stats.getTotalEnrollments()));
        table.addCell(stats.getMostPopularCourse());
        table.setSpacingAfter(18f);
        document.add(table);
    }

    private static void addStudentTable(Document document, List<Student> students) throws DocumentException {
        Paragraph heading = heading("Students");
        document.add(heading);
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        addHeader(table, "ID");
        addHeader(table, "Name");
        addHeader(table, "Email");
        for (Student student : students) {
            table.addCell(String.valueOf(student.getId()));
            table.addCell(student.getName());
            table.addCell(student.getEmail());
        }
        table.setSpacingAfter(16f);
        document.add(table);
    }

    private static void addCourseTable(Document document, List<Course> courses) throws DocumentException {
        document.add(heading("Courses"));
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        addHeader(table, "ID");
        addHeader(table, "Name");
        addHeader(table, "Instructor");
        addHeader(table, "Schedule");
        addHeader(table, "Capacity");
        addHeader(table, "Enrolled");
        for (Course course : courses) {
            table.addCell(String.valueOf(course.getId()));
            table.addCell(course.getName());
            table.addCell(course.getInstructor());
            table.addCell(course.getScheduleText());
            table.addCell(String.valueOf(course.getCapacity()));
            table.addCell(String.valueOf(course.getEnrolledCount()));
        }
        table.setSpacingAfter(16f);
        document.add(table);
    }

    private static void addEnrollmentTable(Document document, List<Enrollment> enrollments) throws DocumentException {
        document.add(heading("Enrollments"));
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        addHeader(table, "Student ID");
        addHeader(table, "Student");
        addHeader(table, "Course ID");
        addHeader(table, "Course");
        addHeader(table, "Schedule");
        addHeader(table, "Date");
        for (Enrollment enrollment : enrollments) {
            table.addCell(String.valueOf(enrollment.getStudentId()));
            table.addCell(enrollment.getStudentName());
            table.addCell(String.valueOf(enrollment.getCourseId()));
            table.addCell(enrollment.getCourseName());
            table.addCell(enrollment.getScheduleText());
            table.addCell(enrollment.getEnrolledAt());
        }
        document.add(table);
    }

    private static Paragraph heading(String text) {
        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph heading = new Paragraph(text, headingFont);
        heading.setSpacingBefore(6f);
        heading.setSpacingAfter(8f);
        return heading;
    }

    private static void addHeader(PdfPTable table, String value) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        PdfPCell cell = new PdfPCell(new Phrase(value, headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}
