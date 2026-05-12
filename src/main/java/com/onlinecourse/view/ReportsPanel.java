package com.onlinecourse.view;

import com.onlinecourse.controller.DashboardController;
import com.onlinecourse.controller.ReportController;
import com.onlinecourse.model.DashboardStats;
import com.onlinecourse.utils.ThemeManager;
import com.onlinecourse.utils.UiUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Path;

public class ReportsPanel extends JPanel implements Refreshable {
    private final ReportController reportController = new ReportController();
    private final DashboardController dashboardController = new DashboardController();
    private final JLabel studentsValue = new JLabel("0");
    private final JLabel coursesValue = new JLabel("0");
    private final JLabel enrollmentsValue = new JLabel("0");
    private final JLabel popularValue = new JLabel("-");

    public ReportsPanel() {
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(18, 18));
        UiUtils.pad(this, 8, 24, 24, 24);

        JPanel summary = new JPanel(new GridLayout(1, 4, 16, 16));
        summary.add(summaryCard("Students", studentsValue));
        summary.add(summaryCard("Courses", coursesValue));
        summary.add(summaryCard("Enrollments", enrollmentsValue));
        summary.add(summaryCard("Popular Course", popularValue));

        JPanel exports = new JPanel(new GridLayout(2, 2, 14, 14));
        exports.putClientProperty("surface", Boolean.TRUE);
        exports.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.palette().border()),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JButton studentsCsv = UiUtils.primaryButton("Export Students CSV");
        JButton coursesCsv = UiUtils.primaryButton("Export Courses CSV");
        JButton enrollmentsCsv = UiUtils.primaryButton("Export Enrollments CSV");
        JButton summaryPdf = UiUtils.neutralButton("Export Summary PDF");
        studentsCsv.addActionListener(event -> exportStudentsCsv());
        coursesCsv.addActionListener(event -> exportCoursesCsv());
        enrollmentsCsv.addActionListener(event -> exportEnrollmentsCsv());
        summaryPdf.addActionListener(event -> exportSummaryPdf());
        exports.add(studentsCsv);
        exports.add(coursesCsv);
        exports.add(enrollmentsCsv);
        exports.add(summaryPdf);

        JTextArea notes = new JTextArea("""
                Reports are generated from the live SQLite database.
                CSV files are useful for spreadsheets, while the PDF summary is ready for project submission.
                """);
        notes.setEditable(false);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.palette().border()),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        add(summary, BorderLayout.NORTH);
        add(exports, BorderLayout.CENTER);
        add(notes, BorderLayout.SOUTH);
    }

    private JPanel summaryCard(String label, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.putClientProperty("surface", Boolean.TRUE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.palette().border()),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        JLabel title = new JLabel(label);
        title.setForeground(ThemeManager.palette().mutedText());
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 22f));
        card.add(title, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refreshData() {
        try {
            DashboardStats stats = dashboardController.getStats();
            studentsValue.setText(String.valueOf(stats.getTotalStudents()));
            coursesValue.setText(String.valueOf(stats.getTotalCourses()));
            enrollmentsValue.setText(String.valueOf(stats.getTotalEnrollments()));
            popularValue.setText(stats.getMostPopularCourse());
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void exportStudentsCsv() {
        exportCsv("students-report.csv", path -> reportController.exportStudentsCsv(path));
    }

    private void exportCoursesCsv() {
        exportCsv("courses-report.csv", path -> reportController.exportCoursesCsv(path));
    }

    private void exportEnrollmentsCsv() {
        exportCsv("enrollments-report.csv", path -> reportController.exportEnrollmentsCsv(path));
    }

    private void exportSummaryPdf() {
        Path path = choosePath("course-enrollment-summary.pdf", "PDF files", "pdf");
        if (path == null) {
            return;
        }
        try {
            reportController.exportSummaryPdf(ensureExtension(path, ".pdf"));
            UiUtils.showInfo(this, "PDF report exported successfully.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void exportCsv(String defaultName, ExportAction action) {
        Path path = choosePath(defaultName, "CSV files", "csv");
        if (path == null) {
            return;
        }
        try {
            action.export(ensureExtension(path, ".csv"));
            UiUtils.showInfo(this, "CSV report exported successfully.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private Path choosePath(String defaultName, String description, String extension) {
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        JFileChooser chooser = new JFileChooser(reportsDir);
        chooser.setDialogTitle("Export Report");
        chooser.setSelectedFile(new File(defaultName));
        chooser.setFileFilter(new FileNameExtensionFilter(description, extension));
        int result = chooser.showSaveDialog(this);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile().toPath() : null;
    }

    private Path ensureExtension(Path path, String extension) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(extension) ? path : Path.of(path + extension);
    }

    @FunctionalInterface
    private interface ExportAction {
        void export(Path path) throws Exception;
    }
}
