package com.onlinecourse.utils;

import java.util.regex.Pattern;

public final class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private ValidationUtils() {
    }

    public static String requireText(String value, String fieldName) throws AppException {
        if (value == null || value.trim().isEmpty()) {
            throw new AppException(fieldName + " is required.");
        }
        return value.trim();
    }

    public static String requireEmail(String value) throws AppException {
        String email = requireText(value, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new AppException("Please enter a valid email address.");
        }
        return email.toLowerCase();
    }

    public static int requirePositiveInt(String value, String fieldName) throws AppException {
        String text = requireText(value, fieldName);
        try {
            int number = Integer.parseInt(text);
            if (number <= 0) {
                throw new AppException(fieldName + " must be greater than zero.");
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new AppException(fieldName + " must be a whole number.", ex);
        }
    }

    public static String requireDay(String value) throws AppException {
        String day = requireText(value, "Course day");
        for (String validDay : DAYS) {
            if (validDay.equalsIgnoreCase(day)) {
                return validDay;
            }
        }
        throw new AppException("Course day must be a valid weekday.");
    }

    public static String requireTime(String value, String fieldName) throws AppException {
        String time = requireText(value, fieldName);
        if (!TIME_PATTERN.matcher(time).matches()) {
            throw new AppException(fieldName + " must use 24-hour HH:mm format.");
        }
        return time;
    }

    public static void requireTimeRange(String startTime, String endTime) throws AppException {
        int start = toMinutes(startTime);
        int end = toMinutes(endTime);
        if (end <= start) {
            throw new AppException("Course end time must be after the start time.");
        }
    }

    public static int toMinutes(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]) * 60 + Integer.parseInt(pieces[1]);
    }

    public static String[] days() {
        return DAYS.clone();
    }
}
