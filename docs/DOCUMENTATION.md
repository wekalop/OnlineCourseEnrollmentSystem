# Documentation

## Overview

The Online Course Enrollment System is a Java Swing desktop application that demonstrates OOP, MVC-style separation, SQLite persistence, input validation, exception handling, tables, search, charts, login, light/dark mode, course scheduling, and report export.

## Architecture

The project follows a practical MVC structure:

- `model`: Plain Java objects such as `Student`, `Course`, `Enrollment`, `DashboardStats`, and `Admin`.
- `view`: Swing screens and panels such as `LoginFrame`, `MainFrame`, `StudentPortalFrame`, `DashboardPanel`, `StudentPanel`, `CoursePanel`, `EnrollmentPanel`, and `ReportsPanel`.
- `controller`: Application actions and validation flow. Controllers receive GUI input, validate it, and call DAOs.
- `database`: SQLite connection, schema initialization, and DAO classes.
- `utils`: Shared validation, theming, password hashing, CSV export, PDF export, and UI helpers.

## Important Classes

- `Main`: Starts the application.
- `DatabaseManager`: Creates SQLite connections and enables foreign keys.
- `SchemaInitializer`: Runs schema/sample scripts and seeds the default admin.
- `StudentDAO`, `CourseDAO`, `EnrollmentDAO`: Database operations for core entities.
- `DashboardDAO`: Dashboard statistics and enrollment counts.
- `AuthController`: Admin login validation.
- `StudentPortalController`: Student course browsing, enrollment, unenrollment, and personal enrollment records.
- `StudentController`, `CourseController`, `EnrollmentController`: Business logic and validation.
- `ReportController`: Coordinates PDF and CSV exports.
- `ThemeManager`: Dark/light color palette and component styling.

## Database Design

```sql
students(id, name, email, password_hash, salt)
courses(id, name, instructor, capacity, day_of_week, start_time, end_time)
enrollments(student_id, course_id)
admins(id, username, password_hash, salt, created_at)
```

Key constraints:

- Student emails are unique.
- Student passwords are stored as salted hashes.
- A student can activate login for an existing admin-created student record by registering with the same email address.
- Course capacity must be greater than zero.
- Course start and end times use 24-hour `HH:mm` format.
- Course end time must be after start time.
- Enrollment primary key is `(student_id, course_id)`, preventing duplicates.
- Foreign keys cascade deletes from students/courses to enrollments.
- Enrollment rejects schedule overlaps for the same student.
- Course timing updates are blocked when they would create conflicts for already-enrolled students.
- Course add/update rejects overlapping courses for the same instructor.

## Validation

The application validates:

- Empty name, email, course, instructor, username, and password fields
- Email format
- Numeric capacity
- Positive capacity
- Valid weekday
- Start/end time format
- End time after start time
- Selected row requirements for update/delete
- Student registration password length and confirmation match
- Student login credentials
- Duplicate enrollments
- Full courses
- Student schedule conflicts
- Instructor schedule conflicts
- Admin course timing changes that would break existing student schedules
- Course capacity updates that would be lower than the existing enrollment count

## Exception Handling

All database and validation failures are converted into user-friendly `AppException` messages. Swing screens catch exceptions and display them with `JOptionPane`.

## Reports

The Reports panel can export:

- Students CSV
- Courses CSV
- Enrollments CSV
- PDF summary report

Exports are generated from the live SQLite database and default to the `reports/` folder.

## Suggested Demonstration Steps

1. Start the application and log in with `admin / admin123`.
2. Open the Dashboard and show the statistics/chart.
3. Add a new student.
4. Add a new course with day, start time, and end time.
5. Enroll the student in the course.
6. Try enrolling the same student again to show duplicate prevention.
7. Set a small course capacity and show the full-course validation.
8. Use search/filter fields in Students, Courses, and Enrollments.
9. Toggle dark mode.
10. Register a student account from the Login screen.
11. Log in as that student and enroll in an admin-created course.
12. Try enrolling in a course with an overlapping time to show schedule conflict prevention.
13. Unenroll from the student portal and show the updated personal schedule.
14. Toggle light mode.
15. Export CSV and PDF reports.
