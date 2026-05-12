package com.onlinecourse.view;

import com.onlinecourse.controller.DashboardController;
import com.onlinecourse.model.DashboardStats;
import com.onlinecourse.utils.ThemeManager;
import com.onlinecourse.utils.UiUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;

public class DashboardPanel extends JPanel implements Refreshable {
    private final DashboardController dashboardController = new DashboardController();
    private final JLabel totalStudents = new JLabel("0");
    private final JLabel totalCourses = new JLabel("0");
    private final JLabel totalEnrollments = new JLabel("0");
    private final JLabel mostPopularCourse = new JLabel("-");
    private final JPanel chartHolder = new JPanel(new BorderLayout());

    public DashboardPanel() {
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(0, 18));
        UiUtils.pad(this, 8, 24, 24, 24);

        JPanel statGrid = new JPanel(new GridLayout(1, 4, 16, 16));
        statGrid.add(createStatCard("Total Students", totalStudents));
        statGrid.add(createStatCard("Total Courses", totalCourses));
        statGrid.add(createStatCard("Total Enrollments", totalEnrollments));
        statGrid.add(createStatCard("Most Popular", mostPopularCourse));

        chartHolder.putClientProperty("surface", Boolean.TRUE);
        chartHolder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.palette().border()),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        add(statGrid, BorderLayout.NORTH);
        add(chartHolder, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String label, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.putClientProperty("surface", Boolean.TRUE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.palette().border()),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel(label);
        title.setForeground(ThemeManager.palette().mutedText());
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 26f));

        card.add(title, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refreshData() {
        try {
            DashboardStats stats = dashboardController.getStats();
            totalStudents.setText(String.valueOf(stats.getTotalStudents()));
            totalCourses.setText(String.valueOf(stats.getTotalCourses()));
            totalEnrollments.setText(String.valueOf(stats.getTotalEnrollments()));
            mostPopularCourse.setText(stats.getMostPopularCourse());
            rebuildChart(stats.getEnrollmentsByCourse());
        } catch (Exception ex) {
            UiUtils.showError(this, ex);
        }
    }

    private void rebuildChart(Map<String, Integer> values) {
        // JFreeChart renders the dashboard chart from the live enrollment counts.
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (values.isEmpty()) {
            dataset.addValue(0, "Enrollments", "No courses");
        } else {
            values.forEach((course, count) -> dataset.addValue(count, "Enrollments", course));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Enrollments by Course",
                "Course",
                "Students",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        applyChartTheme(chart);

        chartHolder.removeAll();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartHolder.add(chartPanel, BorderLayout.CENTER);
        chartHolder.revalidate();
        chartHolder.repaint();
    }

    private void applyChartTheme(JFreeChart chart) {
        ThemeManager.Palette p = ThemeManager.palette();
        chart.setBackgroundPaint(p.surface());
        chart.getTitle().setPaint(p.text());
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(p.surface());
        plot.setDomainGridlinePaint(p.border());
        plot.setRangeGridlinePaint(p.border());
        plot.getRenderer().setSeriesPaint(0, p.accent());
        plot.getDomainAxis().setLabelPaint(p.text());
        plot.getDomainAxis().setTickLabelPaint(p.text());
        plot.getRangeAxis().setLabelPaint(p.text());
        plot.getRangeAxis().setTickLabelPaint(p.text());
        plot.setOutlinePaint(new Color(0, 0, 0, 0));
    }
}
