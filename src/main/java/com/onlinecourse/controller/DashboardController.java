package com.onlinecourse.controller;

import com.onlinecourse.database.DashboardDAO;
import com.onlinecourse.model.DashboardStats;
import com.onlinecourse.utils.AppException;

public class DashboardController {
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public DashboardStats getStats() throws AppException {
        return dashboardDAO.loadStats();
    }
}
