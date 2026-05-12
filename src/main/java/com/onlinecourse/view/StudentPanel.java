package com.onlinecourse.view;

import com.onlinecourse.controller.StudentController;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.ThemeManager;
import com.onlinecourse.utils.UiUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

public class StudentPanel extends JPanel implements Refreshable {
    private final StudentController studentController = new StudentController();
    private final Runnable onDataChanged;
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField searchField = new JTextField(24);
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = UiUtils.attachFilter(table, tableModel);
    private int selectedStudentId;

    public StudentPanel(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(18, 18));
        UiUtils.pad(this, 8, 24, 24, 24);

        JPanel form = buildForm();
        JPanel tablePanel = buildTablePanel();

        add(form, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
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
        form.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        form.add(nameField, gbc);
        gbc.gridx = 2;
        form.add(new JLabel("Email"), gbc);
        gbc.gridx = 3;
        form.add(emailField, gbc);

        JPanel buttons = new JPanel();
        buttons.putClientProperty("surface", Boolean.TRUE);
        JButton addButton = UiUtils.primaryButton("Add");
        JButton updateButton = UiUtils.neutralButton("Update");
        JButton deleteButton = UiUtils.dangerButton("Delete");
        JButton clearButton = UiUtils.neutralButton("Clear");
        addButton.addActionListener(event -> addStudent());
        updateButton.addActionListener(event -> updateStudent());
        deleteButton.addActionListener(event -> deleteStudent());
        clearButton.addActionListener(event -> clearForm());
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 1;
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
                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    emailField.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });
    }

    @Override
    public void refreshData() {
        try {
            loadStudents(studentController.getStudents());
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void loadStudents(List<Student> students) {
        table.clearSelection();
        selectedStudentId = 0;
        tableModel.setRowCount(0);
        for (Student student : students) {
            tableModel.addRow(new Object[]{student.getId(), student.getName(), student.getEmail()});
        }
        filter();
    }

    private void addStudent() {
        try {
            studentController.addStudent(nameField.getText(), emailField.getText());
            clearForm();
            onDataChanged.run();
            UiUtils.showInfo(this, "Student added successfully.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void updateStudent() {
        try {
            studentController.updateStudent(selectedStudentId, nameField.getText(), emailField.getText());
            clearForm();
            onDataChanged.run();
            UiUtils.showInfo(this, "Student updated successfully.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void deleteStudent() {
        if (!UiUtils.confirm(this, "Delete the selected student and their enrollments?")) {
            return;
        }
        try {
            studentController.deleteStudent(selectedStudentId);
            clearForm();
            onDataChanged.run();
            UiUtils.showInfo(this, "Student deleted successfully.");
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void clearForm() {
        selectedStudentId = 0;
        nameField.setText("");
        emailField.setText("");
        table.clearSelection();
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
