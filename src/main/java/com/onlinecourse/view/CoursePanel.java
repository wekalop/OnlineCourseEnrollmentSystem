package com.onlinecourse.view;

import com.onlinecourse.controller.CourseController;
import com.onlinecourse.model.Course;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.ThemeManager;
import com.onlinecourse.utils.UiUtils;
import com.onlinecourse.utils.ValidationUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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

public class CoursePanel extends JPanel implements Refreshable {
    private final CourseController courseController = new CourseController();
    private final Runnable onDataChanged;
    private final JTextField nameField = new JTextField(18);
    private final JTextField instructorField = new JTextField(18);
    private final JTextField capacityField = new JTextField(8);
    private final JComboBox<String> dayCombo = new JComboBox<>(ValidationUtils.days());
    private final JTextField startTimeField = new JTextField("09:00", 6);
    private final JTextField endTimeField = new JTextField("10:30", 6);
    private final JTextField searchField = new JTextField(24);
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Instructor", "Capacity", "Schedule", "Enrolled", "Seats Left"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = UiUtils.attachFilter(table, tableModel);
    private int selectedCourseId;

    public CoursePanel(Runnable onDataChanged) {
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
        form.add(new JLabel("Course"), gbc);
        gbc.gridx = 1;
        form.add(nameField, gbc);
        gbc.gridx = 2;
        form.add(new JLabel("Instructor"), gbc);
        gbc.gridx = 3;
        form.add(instructorField, gbc);
        gbc.gridx = 4;
        form.add(new JLabel("Capacity"), gbc);
        gbc.gridx = 5;
        form.add(capacityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Day"), gbc);
        gbc.gridx = 1;
        form.add(dayCombo, gbc);
        gbc.gridx = 2;
        form.add(new JLabel("Start"), gbc);
        gbc.gridx = 3;
        form.add(startTimeField, gbc);
        gbc.gridx = 4;
        form.add(new JLabel("End"), gbc);
        gbc.gridx = 5;
        form.add(endTimeField, gbc);

        JPanel buttons = new JPanel();
        buttons.putClientProperty("surface", Boolean.TRUE);
        JButton addButton = UiUtils.primaryButton("Add");
        JButton updateButton = UiUtils.neutralButton("Update");
        JButton deleteButton = UiUtils.dangerButton("Delete");
        JButton enrolledButton = UiUtils.neutralButton("View Enrolled");
        JButton clearButton = UiUtils.neutralButton("Clear");
        addButton.addActionListener(event -> addCourse());
        updateButton.addActionListener(event -> updateCourse());
        deleteButton.addActionListener(event -> deleteCourse());
        enrolledButton.addActionListener(event -> showEnrolledStudents());
        clearButton.addActionListener(event -> clearForm());
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(enrolledButton);
        buttons.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
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
                    selectedCourseId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    instructorField.setText(tableModel.getValueAt(row, 2).toString());
                    capacityField.setText(tableModel.getValueAt(row, 3).toString());
                    String[] scheduleParts = parseSchedule(tableModel.getValueAt(row, 4).toString());
                    dayCombo.setSelectedItem(scheduleParts[0]);
                    startTimeField.setText(scheduleParts[1]);
                    endTimeField.setText(scheduleParts[2]);
                }
            }
        });
    }

    @Override
    public void refreshData() {
        try {
            loadCourses(courseController.getCourses());
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void loadCourses(List<Course> courses) {
        table.clearSelection();
        selectedCourseId = 0;
        tableModel.setRowCount(0);
        for (Course course : courses) {
            tableModel.addRow(new Object[]{
                    course.getId(),
                    course.getName(),
                    course.getInstructor(),
                    course.getCapacity(),
                    course.getScheduleText(),
                    course.getEnrolledCount(),
                    course.getSeatsLeft()
            });
        }
        filter();
    }

    private void addCourse() {
        try {
            courseController.addCourse(
                    nameField.getText(),
                    instructorField.getText(),
                    capacityField.getText(),
                    selectedDay(),
                    startTimeField.getText(),
                    endTimeField.getText()
            );
            clearForm();
            onDataChanged.run();
            UiUtils.showInfo(this, "Course added successfully.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void updateCourse() {
        try {
            courseController.updateCourse(
                    selectedCourseId,
                    nameField.getText(),
                    instructorField.getText(),
                    capacityField.getText(),
                    selectedDay(),
                    startTimeField.getText(),
                    endTimeField.getText()
            );
            clearForm();
            onDataChanged.run();
            UiUtils.showInfo(this, "Course updated successfully.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void deleteCourse() {
        if (!UiUtils.confirm(this, "Delete the selected course and its enrollments?")) {
            return;
        }
        try {
            courseController.deleteCourse(selectedCourseId);
            clearForm();
            onDataChanged.run();
            UiUtils.showInfo(this, "Course deleted successfully.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void showEnrolledStudents() {
        try {
            List<Student> students = courseController.getEnrolledStudents(selectedCourseId);
            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Name", "Email"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (Student student : students) {
                model.addRow(new Object[]{student.getId(), student.getName(), student.getEmail()});
            }
            JTable enrolledTable = new JTable(model);
            UiUtils.configureTable(enrolledTable);

            JDialog dialog = new JDialog();
            dialog.setTitle("Enrolled Students");
            dialog.setSize(620, 360);
            dialog.setLocationRelativeTo(this);
            JPanel content = new JPanel(new BorderLayout());
            UiUtils.pad(content, 16, 16, 16, 16);
            content.add(new JScrollPane(enrolledTable), BorderLayout.CENTER);
            dialog.setContentPane(content);
            ThemeManager.applyTo(content);
            dialog.setVisible(true);
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void clearForm() {
        selectedCourseId = 0;
        nameField.setText("");
        instructorField.setText("");
        capacityField.setText("");
        dayCombo.setSelectedIndex(0);
        startTimeField.setText("09:00");
        endTimeField.setText("10:30");
        table.clearSelection();
    }

    private String selectedDay() {
        Object selected = dayCombo.getSelectedItem();
        return selected == null ? "" : selected.toString();
    }

    private String[] parseSchedule(String schedule) {
        String[] fallback = {ValidationUtils.days()[0], "09:00", "10:30"};
        if (schedule == null || schedule.isBlank()) {
            return fallback;
        }
        String[] pieces = schedule.split(" ");
        if (pieces.length < 2 || !pieces[1].contains("-")) {
            return fallback;
        }
        String[] times = pieces[1].split("-");
        if (times.length != 2) {
            return fallback;
        }
        return new String[]{pieces[0], times[0], times[1]};
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
