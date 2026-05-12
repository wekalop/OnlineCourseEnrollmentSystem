package com.onlinecourse.utils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.util.regex.Pattern;

public final class UiUtils {
    private UiUtils() {
    }

    public static JButton primaryButton(String text) {
        return styledButton(text, "primary", ThemeManager.palette().accent(), Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return styledButton(text, "danger", ThemeManager.palette().danger(), Color.WHITE);
    }

    public static JButton neutralButton(String text) {
        ThemeManager.Palette p = ThemeManager.palette();
        return styledButton(text, "neutral", p.input(), p.text());
    }

    private static JButton styledButton(String text, String variant, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.putClientProperty("styled", Boolean.TRUE);
        button.putClientProperty("variant", variant);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        return button;
    }

    public static void configureTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        table.setDefaultRenderer(Object.class, renderer);
    }

    public static TableRowSorter<DefaultTableModel> attachFilter(JTable table, DefaultTableModel model) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        return sorter;
    }

    public static void applyFilter(TableRowSorter<DefaultTableModel> sorter, String query) {
        String value = query == null ? "" : query.trim();
        if (value.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(value)));
        }
    }

    public static void showError(Component parent, Exception exception) {
        JOptionPane.showMessageDialog(parent, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Online Course Enrollment System", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void pad(JComponent component, int top, int left, int bottom, int right) {
        component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }
}
