package com.onlinecourse.view;

import com.onlinecourse.controller.StudentPortalController;
import com.onlinecourse.model.Course;
import com.onlinecourse.model.Enrollment;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.ThemeManager;
import com.onlinecourse.utils.UiUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentPortalFrame extends JFrame {
    private final StudentPortalController controller;
    private final JTextField courseSearchField = new JTextField(22);
    private final DefaultTableModel courseModel = new DefaultTableModel(new Object[]{"ID", "Course", "Instructor", "Schedule", "Capacity", "Enrolled", "Seats Left", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable courseTable = new JTable(courseModel);
    private final TableRowSorter<DefaultTableModel> courseSorter = UiUtils.attachFilter(courseTable, courseModel);
    private final DefaultTableModel enrollmentModel = new DefaultTableModel(new Object[]{"Course ID", "Course", "Schedule", "Enrolled At"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable enrollmentTable = new JTable(enrollmentModel);

    public StudentPortalFrame(Student student) {
        super("Student Portal - Online Course Enrollment System");
        this.controller = new StudentPortalController(student);
        buildUi();
        refreshData();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 660));
        setSize(1120, 720);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(18, 18));
        UiUtils.pad(root, 20, 24, 24, 24);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);
        setContentPane(root);
        ThemeManager.applyTo(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Welcome, " + controller.getStudent().getName());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitle = new JLabel("Browse admin-created courses and manage your enrollments.");
        subtitle.setForeground(ThemeManager.palette().mutedText());

        JPanel text = new JPanel(new BorderLayout(0, 4));
        text.add(title, BorderLayout.NORTH);
        text.add(subtitle, BorderLayout.SOUTH);

        JPanel actions = new JPanel();
        JCheckBox lightMode = new JCheckBox("Light mode", !ThemeManager.isDarkMode());
        JButton refreshButton = UiUtils.neutralButton("Refresh");
        JButton logoutButton = UiUtils.dangerButton("Logout");
        lightMode.addActionListener(event -> {
            ThemeManager.setDarkMode(!lightMode.isSelected());
            ThemeManager.applyTo(getContentPane());
            repaint();
        });
        refreshButton.addActionListener(event -> refreshData());
        logoutButton.addActionListener(event -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        actions.add(lightMode);
        actions.add(refreshButton);
        actions.add(logoutButton);

        header.add(text, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(2, 1, 0, 18));
        content.add(buildCoursePanel());
        content.add(buildEnrollmentPanel());
        return content;
    }

    private JPanel buildCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.putClientProperty("surface", Boolean.TRUE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.palette().border()),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.putClientProperty("surface", Boolean.TRUE);
        JLabel heading = new JLabel("Available Courses");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 16f));
        toolbar.add(heading, BorderLayout.WEST);
        toolbar.add(courseSearchField, BorderLayout.EAST);

        UiUtils.configureTable(courseTable);
        courseSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                UiUtils.applyFilter(courseSorter, courseSearchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                UiUtils.applyFilter(courseSorter, courseSearchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                UiUtils.applyFilter(courseSorter, courseSearchField.getText());
            }
        });

        JButton enrollButton = UiUtils.primaryButton("Enroll Selected Course");
        enrollButton.addActionListener(event -> enrollSelectedCourse());

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);
        panel.add(enrollButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.putClientProperty("surface", Boolean.TRUE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.palette().border()),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel heading = new JLabel("My Weekly Schedule / Enrollment Records");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 16f));
        UiUtils.configureTable(enrollmentTable);

        JButton unenrollButton = UiUtils.dangerButton("Unenroll Selected Course");
        unenrollButton.addActionListener(event -> unenrollSelectedCourse());

        panel.add(heading, BorderLayout.NORTH);
        panel.add(new JScrollPane(enrollmentTable), BorderLayout.CENTER);
        panel.add(unenrollButton, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshData() {
        try {
            List<Enrollment> enrollments = controller.getMyEnrollments();
            Set<Integer> enrolledCourseIds = new HashSet<>();
            enrollmentModel.setRowCount(0);
            for (Enrollment enrollment : enrollments) {
                enrolledCourseIds.add(enrollment.getCourseId());
                enrollmentModel.addRow(new Object[]{
                        enrollment.getCourseId(),
                        enrollment.getCourseName(),
                        enrollment.getScheduleText(),
                        enrollment.getEnrolledAt()
                });
            }

            courseModel.setRowCount(0);
            for (Course course : controller.getAvailableCourses()) {
                String status = statusFor(course, enrolledCourseIds);
                courseModel.addRow(new Object[]{
                        course.getId(),
                        course.getName(),
                        course.getInstructor(),
                        course.getScheduleText(),
                        course.getCapacity(),
                        course.getEnrolledCount(),
                        course.getSeatsLeft(),
                        status
                });
            }
            UiUtils.applyFilter(courseSorter, courseSearchField.getText());
            ThemeManager.applyTo(getContentPane());
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private String statusFor(Course course, Set<Integer> enrolledCourseIds) {
        if (enrolledCourseIds.contains(course.getId())) {
            return "Enrolled";
        }
        return course.getSeatsLeft() > 0 ? "Available" : "Full";
    }

    private void enrollSelectedCourse() {
        try {
            controller.enroll(selectedCourseId(courseTable, courseModel, 0));
            refreshData();
            UiUtils.showInfo(this, "Enrollment completed.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void unenrollSelectedCourse() {
        try {
            controller.unenroll(selectedCourseId(enrollmentTable, enrollmentModel, 0));
            refreshData();
            UiUtils.showInfo(this, "Enrollment removed.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private int selectedCourseId(JTable table, DefaultTableModel model, int idColumn) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0 || viewRow >= table.getRowCount()) {
            return 0;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return Integer.parseInt(model.getValueAt(modelRow, idColumn).toString());
    }
}
