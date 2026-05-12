package com.onlinecourse.view;

import com.onlinecourse.utils.ThemeManager;
import com.onlinecourse.utils.UiUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

public class MainFrame extends JFrame {
    private final DashboardPanel dashboardPanel = new DashboardPanel();
    private final StudentPanel studentPanel = new StudentPanel(this::refreshAll);
    private final CoursePanel coursePanel = new CoursePanel(this::refreshAll);
    private final EnrollmentPanel enrollmentPanel = new EnrollmentPanel(this::refreshAll);
    private final ReportsPanel reportsPanel = new ReportsPanel();
    private final List<Refreshable> refreshables = List.of(dashboardPanel, studentPanel, coursePanel, enrollmentPanel, reportsPanel);
    private final JPanel cards = new JPanel(new CardLayout());
    private final JList<String> navigation = new JList<>(new String[]{"Dashboard", "Students", "Courses", "Enrollment", "Reports"});
    private final JLabel screenTitle = new JLabel("Dashboard");

    public MainFrame(String username) {
        super("Online Course Enrollment System");
        buildUi(username);
        refreshAll();
    }

    private void buildUi(String username) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 740));
        setSize(1280, 780);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createTopBar(username), BorderLayout.NORTH);
        root.add(createCards(), BorderLayout.CENTER);
        setContentPane(root);
        ThemeManager.applyTo(root);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 20));
        sidebar.putClientProperty("surface", Boolean.TRUE);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 18, 22, 18));

        JLabel brand = new JLabel("<html><b>Course<br>Enrollment</b></html>");
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 22f));

        navigation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        navigation.setSelectedIndex(0);
        navigation.setFixedCellHeight(42);
        navigation.setBorder(BorderFactory.createEmptyBorder());
        navigation.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showSelectedCard();
            }
        });

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(new JScrollPane(navigation), BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel createTopBar(String username) {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        screenTitle.setFont(screenTitle.getFont().deriveFont(Font.BOLD, 22f));

        JPanel actions = new JPanel();
        JCheckBox darkToggle = new JCheckBox("Light mode", !ThemeManager.isDarkMode());
        darkToggle.addActionListener(event -> {
            ThemeManager.setDarkMode(!darkToggle.isSelected());
            SwingUtilities.updateComponentTreeUI(this);
            ThemeManager.applyTo(getContentPane());
            refreshAll();
        });
        JButton refreshButton = UiUtils.neutralButton("Refresh");
        refreshButton.addActionListener(event -> refreshAll());
        JButton logoutButton = UiUtils.dangerButton("Logout");
        logoutButton.addActionListener(event -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        JLabel userLabel = new JLabel("Signed in as " + username);
        userLabel.setForeground(ThemeManager.palette().mutedText());

        actions.add(userLabel);
        actions.add(darkToggle);
        actions.add(refreshButton);
        actions.add(logoutButton);

        topBar.add(screenTitle, BorderLayout.WEST);
        topBar.add(actions, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createCards() {
        cards.add(dashboardPanel, "Dashboard");
        cards.add(studentPanel, "Students");
        cards.add(coursePanel, "Courses");
        cards.add(enrollmentPanel, "Enrollment");
        cards.add(reportsPanel, "Reports");
        return cards;
    }

    private void showSelectedCard() {
        String selected = navigation.getSelectedValue();
        CardLayout layout = (CardLayout) cards.getLayout();
        layout.show(cards, selected);
        updateTitle(selected);
        refreshSelected(selected);
    }

    private void updateTitle(String selected) {
        screenTitle.setText(selected);
    }

    private void refreshSelected(String selected) {
        switch (selected) {
            case "Dashboard" -> dashboardPanel.refreshData();
            case "Students" -> studentPanel.refreshData();
            case "Courses" -> coursePanel.refreshData();
            case "Enrollment" -> enrollmentPanel.refreshData();
            case "Reports" -> reportsPanel.refreshData();
            default -> refreshAll();
        }
    }

    private void refreshAll() {
        for (Refreshable refreshable : refreshables) {
            refreshable.refreshData();
        }
        ThemeManager.applyTo(getContentPane());
        repaint();
    }
}
