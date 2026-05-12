package com.onlinecourse;

import com.onlinecourse.database.DatabaseManager;
import com.onlinecourse.utils.ThemeManager;
import com.onlinecourse.view.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                ThemeManager.setDarkMode(true);
                // Create tables and sample data before any screen touches the database.
                DatabaseManager.initializeDatabase();
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Application could not start: " + ex.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
