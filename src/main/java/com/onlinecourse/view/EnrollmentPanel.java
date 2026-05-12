package com.onlinecourse.view;

import com.onlinecourse.controller.EnrollmentController;
import com.onlinecourse.model.Course;
import com.onlinecourse.model.Enrollment;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.ComboItem;
import com.onlinecourse.utils.ThemeManager;
import com.onlinecourse.utils.UiUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class EnrollmentPanel extends JPanel implements Refreshable {
    private final EnrollmentController enrollmentController = new EnrollmentController();
    private final Runnable onDataChanged;
    private final JComboBox<ComboItem<Student>> studentCombo = new JComboBox<>();
    private final JComboBox<ComboItem<Course>> courseCombo = new JComboBox<>();
    private final JTextField searchField = new JTextField(24);
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Student ID", "Student", "Course ID", "Course", "Schedule", "Enrolled At"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = UiUtils.attachFilter(table, tableModel);
    private int selectedStudentId;
    private int selectedCourseId;

    public EnrollmentPanel(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(18, 18));
        UiUtils.pad(this, 8, 24, 24, 24);
        add(buildForm(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        addSearchListener();
        addSelectionListener();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.putClientProperty("surface", Boolean.TRUE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.palette().border()),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Student"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(studentCombo, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        form.add(new JLabel("Course"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        form.add(courseCombo, gbc);

        JPanel buttons = new JPanel();
        buttons.putClientProperty("surface", Boolean.TRUE);
        JButton enrollButton = UiUtils.primaryButton("Enroll");
        JButton unenrollButton = UiUtils.dangerButton("Unenroll");
        JButton clearButton = UiUtils.neutralButton("Clear");
        enrollButton.addActionListener(event -> enroll());
        unenrollButton.addActionListener(event -> unenroll());
        clearButton.addActionListener(event -> clearSelection());
        buttons.add(enrollButton);
        buttons.add(unenrollButton);
        buttons.add(clearButton);

        gbc.gridx = 4;
        gbc.weightx = 0;
        form.add(buttons, gbc);
        return form;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.putClientProperty("surface", Boolean.TRUE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.palette().border()),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.putClientProperty("surface", Boolean.TRUE);
        toolbar.add(new JLabel("Search"), BorderLayout.WEST);
        toolbar.add(searchField, BorderLayout.EAST);

        UiUtils.configureTable(table);
        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void addSearchListener() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }
        });
    }

    private void addSelectionListener() {
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int row = selectedModelRow();
                if (row >= 0) {
                    selectedStudentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    selectedCourseId = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
                    selectComboById(studentCombo, selectedStudentId);
                    selectComboById(courseCombo, selectedCourseId);
                }
            }
        });
    }

    @Override
    public void refreshData() {
        try {
            loadCombos(enrollmentController.getStudents(), enrollmentController.getCourses());
            loadEnrollments(enrollmentController.getEnrollments());
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void loadCombos(List<Student> students, List<Course> courses) {
        int currentStudent = selectedStudentId;
        int currentCourse = selectedCourseId;

        studentCombo.removeAllItems();
        for (Student student : students) {
            studentCombo.addItem(new ComboItem<>(student.getId(), student.getId() + " - " + student.getName(), student));
        }

        courseCombo.removeAllItems();
        for (Course course : courses) {
            courseCombo.addItem(new ComboItem<>(
                    course.getId(),
                    course.getId() + " - " + course.getName() + " (" + course.getSeatsLeft() + " seats left)",
                    course
            ));
        }

        selectComboById(studentCombo, currentStudent);
        selectComboById(courseCombo, currentCourse);
    }

    private void loadEnrollments(List<Enrollment> enrollments) {
        table.clearSelection();
        selectedStudentId = 0;
        selectedCourseId = 0;
        tableModel.setRowCount(0);
        for (Enrollment enrollment : enrollments) {
            tableModel.addRow(new Object[]{
                    enrollment.getStudentId(),
                    enrollment.getStudentName(),
                    enrollment.getCourseId(),
                    enrollment.getCourseName(),
                    enrollment.getScheduleText(),
                    enrollment.getEnrolledAt()
            });
        }
        filter();
    }

    private void enroll() {
        try {
            enrollmentController.enroll(selectedId(studentCombo), selectedId(courseCombo));
            clearSelection();
            onDataChanged.run();
            UiUtils.showInfo(this, "Student enrolled successfully.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void unenroll() {
        try {
            int studentId = selectedStudentId > 0 ? selectedStudentId : selectedId(studentCombo);
            int courseId = selectedCourseId > 0 ? selectedCourseId : selectedId(courseCombo);
            enrollmentController.unenroll(studentId, courseId);
            clearSelection();
            onDataChanged.run();
            UiUtils.showInfo(this, "Student unenrolled successfully.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private int selectedId(JComboBox<? extends ComboItem<?>> comboBox) {
        ComboItem<?> item = (ComboItem<?>) comboBox.getSelectedItem();
        return item == null ? 0 : item.getId();
    }

    private void selectComboById(JComboBox<? extends ComboItem<?>> comboBox, int id) {
        if (id <= 0) {
            return;
        }
        for (int index = 0; index < comboBox.getItemCount(); index++) {
            ComboItem<?> item = comboBox.getItemAt(index);
            if (item.getId() == id) {
                comboBox.setSelectedIndex(index);
                return;
            }
        }
    }

    private void clearSelection() {
        selectedStudentId = 0;
        selectedCourseId = 0;
        table.clearSelection();
        if (studentCombo.getItemCount() > 0) {
            studentCombo.setSelectedIndex(0);
        }
        if (courseCombo.getItemCount() > 0) {
            courseCombo.setSelectedIndex(0);
        }
    }

    private int selectedModelRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0 || viewRow >= table.getRowCount()) {
            return -1;
        }
        return table.convertRowIndexToModel(viewRow);
    }

    private void filter() {
        UiUtils.applyFilter(sorter, searchField.getText());
    }
}
