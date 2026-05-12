package com.onlinecourse.view;

import com.onlinecourse.controller.AuthController;
import com.onlinecourse.model.Student;
import com.onlinecourse.utils.AppException;
import com.onlinecourse.utils.ThemeManager;
import com.onlinecourse.utils.UiUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginFrame extends JFrame {
    private final AuthController authController = new AuthController();
    private final JTextField adminUsernameField = new JTextField("admin", 18);
    private final JPasswordField adminPasswordField = new JPasswordField("admin123", 18);
    private final JTextField studentEmailField = new JTextField(18);
    private final JPasswordField studentPasswordField = new JPasswordField(18);
    private final JTextField registerNameField = new JTextField(18);
    private final JTextField registerEmailField = new JTextField(18);
    private final JPasswordField registerPasswordField = new JPasswordField(18);
    private final JPasswordField registerConfirmField = new JPasswordField(18);

    public LoginFrame() {
        super("Online Course Enrollment System - Login");
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        UiUtils.pad(root, 28, 36, 28, 36);

        JLabel title = new JLabel("Online Course Enrollment System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitle = new JLabel("Admin and student access");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 14f));
        subtitle.setForeground(ThemeManager.palette().mutedText());

        JPanel heading = new JPanel(new BorderLayout(0, 6));
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Admin Login", buildAdminLoginPanel());
        tabs.addTab("Student Login", buildStudentLoginPanel());
        tabs.addTab("Student Register", buildStudentRegisterPanel());

        JLabel hint = new JLabel("Admin default: admin / admin123");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 12f));
        hint.setForeground(ThemeManager.palette().mutedText());
        JCheckBox lightMode = new JCheckBox("Light mode", !ThemeManager.isDarkMode());
        lightMode.addActionListener(event -> {
            ThemeManager.setDarkMode(!lightMode.isSelected());
            ThemeManager.applyTo(getContentPane());
            repaint();
        });
        JPanel footer = new JPanel(new BorderLayout());
        footer.add(hint, BorderLayout.WEST);
        footer.add(lightMode, BorderLayout.EAST);

        root.add(heading, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
        ThemeManager.applyTo(root);
    }

    private JPanel buildPanelShell() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.putClientProperty("surface", Boolean.TRUE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.palette().border()),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        return panel;
    }

    private JPanel buildAdminLoginPanel() {
        JPanel form = buildPanelShell();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        form.add(adminUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        form.add(adminPasswordField, gbc);

        JButton loginButton = UiUtils.primaryButton("Login");
        loginButton.addActionListener(event -> attemptAdminLogin());
        adminPasswordField.addActionListener(event -> attemptAdminLogin());

        gbc.gridx = 1;
        gbc.gridy = 2;
        form.add(loginButton, gbc);
        return form;
    }

    private JPanel buildStudentLoginPanel() {
        JPanel form = buildPanelShell();
        GridBagConstraints gbc = baseConstraints();

        form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        form.add(studentEmailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        form.add(studentPasswordField, gbc);

        JButton loginButton = UiUtils.primaryButton("Login as Student");
        loginButton.addActionListener(event -> attemptStudentLogin());
        studentPasswordField.addActionListener(event -> attemptStudentLogin());
        gbc.gridx = 1;
        gbc.gridy = 2;
        form.add(loginButton, gbc);
        return form;
    }

    private JPanel buildStudentRegisterPanel() {
        JPanel form = buildPanelShell();
        GridBagConstraints gbc = baseConstraints();

        form.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        form.add(registerNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        form.add(registerEmailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        form.add(registerPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(new JLabel("Confirm Password"), gbc);
        gbc.gridx = 1;
        form.add(registerConfirmField, gbc);

        JButton registerButton = UiUtils.primaryButton("Create Account");
        registerButton.addActionListener(event -> registerStudent());
        gbc.gridx = 1;
        gbc.gridy = 4;
        form.add(registerButton, gbc);
        return form;
    }

    private GridBagConstraints baseConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        return gbc;
    }

    private void attemptAdminLogin() {
        try {
            authController.login(adminUsernameField.getText(), adminPasswordField.getPassword());
            String username = adminUsernameField.getText().trim();
            dispose();
            new MainFrame(username).setVisible(true);
        } catch (AppException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void attemptStudentLogin() {
        try {
            Student student = authController.loginStudent(studentEmailField.getText(), studentPasswordField.getPassword());
            dispose();
            new StudentPortalFrame(student).setVisible(true);
        } catch (AppException ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void registerStudent() {
        try {
            Student student = authController.registerStudent(
                    registerNameField.getText(),
                    registerEmailField.getText(),
                    registerPasswordField.getPassword(),
                    registerConfirmField.getPassword()
            );
            dispose();
            new StudentPortalFrame(student).setVisible(true);
        } catch (AppException ex) {
            UiUtils.showError(this, ex);
        }
    }
}
