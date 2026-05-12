package com.onlinecourse.utils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

public final class ThemeManager {
    private static boolean darkMode = true;

    public static final class Palette {
        private final Color background;
        private final Color surface;
        private final Color input;
        private final Color text;
        private final Color mutedText;
        private final Color accent;
        private final Color danger;
        private final Color border;

        private Palette(Color background, Color surface, Color input, Color text, Color mutedText, Color accent, Color danger, Color border) {
            this.background = background;
            this.surface = surface;
            this.input = input;
            this.text = text;
            this.mutedText = mutedText;
            this.accent = accent;
            this.danger = danger;
            this.border = border;
        }

        public Color background() {
            return background;
        }

        public Color surface() {
            return surface;
        }

        public Color input() {
            return input;
        }

        public Color text() {
            return text;
        }

        public Color mutedText() {
            return mutedText;
        }

        public Color accent() {
            return accent;
        }

        public Color danger() {
            return danger;
        }

        public Color border() {
            return border;
        }
    }

    private ThemeManager() {
    }

    public static void setDarkMode(boolean enabled) {
        darkMode = enabled;
        applyDefaults();
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static Palette palette() {
        // Centralized palette keeps all screens visually consistent.
        if (darkMode) {
            return new Palette(
                    new Color(23, 28, 34),
                    new Color(32, 38, 46),
                    new Color(42, 49, 58),
                    new Color(236, 239, 244),
                    new Color(171, 180, 191),
                    new Color(56, 189, 148),
                    new Color(239, 68, 68),
                    new Color(72, 84, 96)
            );
        }
        return new Palette(
                new Color(244, 247, 250),
                Color.WHITE,
                new Color(248, 250, 252),
                new Color(30, 41, 59),
                new Color(100, 116, 139),
                new Color(14, 116, 144),
                new Color(220, 38, 38),
                new Color(203, 213, 225)
        );
    }

    public static void applyDefaults() {
        Palette p = palette();
        UIManager.put("Panel.background", p.background());
        UIManager.put("OptionPane.background", p.surface());
        UIManager.put("OptionPane.messageForeground", p.text());
        UIManager.put("Label.foreground", p.text());
        UIManager.put("Table.background", p.surface());
        UIManager.put("Table.foreground", p.text());
        UIManager.put("Table.selectionBackground", p.accent());
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", p.border());
        UIManager.put("TextField.background", p.input());
        UIManager.put("TextField.foreground", p.text());
        UIManager.put("PasswordField.background", p.input());
        UIManager.put("PasswordField.foreground", p.text());
        UIManager.put("ComboBox.background", p.input());
        UIManager.put("ComboBox.foreground", p.text());
    }

    public static void applyTo(Component component) {
        Palette p = palette();
        if (component instanceof JPanel panel) {
            boolean surface = Boolean.TRUE.equals(panel.getClientProperty("surface"));
            panel.setBackground(surface ? p.surface() : p.background());
        } else if (component instanceof JLabel) {
            component.setForeground(p.text());
        } else if (component instanceof JPasswordField field) {
            field.setBackground(p.input());
            field.setForeground(p.text());
            field.setCaretColor(p.text());
            field.setBorder(inputBorder());
        } else if (component instanceof JTextField field) {
            field.setBackground(p.input());
            field.setForeground(p.text());
            field.setCaretColor(p.text());
            field.setBorder(inputBorder());
        } else if (component instanceof JTextArea area) {
            area.setBackground(p.input());
            area.setForeground(p.text());
            area.setCaretColor(p.text());
            area.setBorder(inputBorder());
        } else if (component instanceof JComboBox<?>) {
            component.setBackground(p.input());
            component.setForeground(p.text());
        } else if (component instanceof JList<?>) {
            component.setBackground(p.surface());
            component.setForeground(p.text());
        } else if (component instanceof JCheckBox checkBox) {
            checkBox.setBackground(p.background());
            checkBox.setForeground(p.text());
        } else if (component instanceof JTable table) {
            table.setBackground(p.surface());
            table.setForeground(p.text());
            table.setSelectionBackground(p.accent());
            table.setSelectionForeground(Color.WHITE);
            table.setGridColor(p.border());
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                header.setBackground(p.input());
                header.setForeground(p.text());
            }
        } else if (component instanceof JScrollPane scrollPane) {
            scrollPane.getViewport().setBackground(p.surface());
            scrollPane.setBorder(BorderFactory.createLineBorder(p.border()));
        } else if (component instanceof JButton button) {
            if (button.getClientProperty("styled") != null) {
                String variant = String.valueOf(button.getClientProperty("variant"));
                if ("primary".equals(variant)) {
                    button.setBackground(p.accent());
                    button.setForeground(Color.WHITE);
                } else if ("danger".equals(variant)) {
                    button.setBackground(p.danger());
                    button.setForeground(Color.WHITE);
                } else {
                    button.setBackground(p.input());
                    button.setForeground(p.text());
                }
            } else {
                button.setBackground(p.input());
                button.setForeground(p.text());
            }
            button.setOpaque(true);
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyTo(child);
            }
        }
    }

    public static Border inputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(palette().border()),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );
    }
}
