# Online Course Enrollment System

A complete Java Swing desktop application for managing students, courses, schedules, and enrollments with SQLite persistence, dashboard statistics, JFreeChart charts, admin/student login, validation, light/dark mode, and CSV/PDF reporting.

## Default Admin Login

- Username: `admin`
- Password: `admin123`

Students can create their own account from the Student Register tab on the login screen. If an admin already created a student record with the same email, registration activates login for that existing student record.

## Tech Stack

- Java 17+
- Java Swing
- SQLite with JDBC
- JFreeChart
- OpenPDF
- Maven

## Run The Project

1. Install JDK 17 or newer.
2. Install Maven.
3. Open a terminal in this folder.
4. Run:

```bash
mvn clean compile
mvn exec:java
```

To create a runnable shaded JAR:

```bash
mvn clean package
java -jar target/online-course-enrollment-system-1.0.0.jar
```

The database is created at `data/course_enrollment.db`. To use a different database path:

```bash
mvn exec:java -Dcourse.db.path=my-data/course_enrollment.db
```

## Project Structure

```text
online-course-enrollment-system/
|-- pom.xml
|-- README.md
|-- sql/
|   |-- schema.sql
|   `-- sample_data.sql
|-- docs/
|   |-- DOCUMENTATION.md
|   `-- SUGGESTED_SCREENSHOTS.md
`-- src/main/
    |-- java/com/onlinecourse/
    |   |-- Main.java
    |   |-- controller/
    |   |-- database/
    |   |-- model/
    |   |-- utils/
    |   `-- view/
    `-- resources/sql/
        |-- schema.sql
        `-- sample_data.sql
```

## Main Features

- Admin login screen
- Student login and registration screen
- Dashboard with total students, courses, enrollments, most popular course, and a JFreeChart bar chart
- Student CRUD with email validation, search, JTable sorting/filtering
- Course CRUD with capacity validation, search, JTable sorting/filtering
- Admin course timing with weekday, start time, and end time
- View enrolled students for a course
- Enroll and unenroll students
- Student portal for browsing admin-created courses and managing personal enrollment records
- Student weekly schedule view sorted by day and time
- Duplicate enrollment prevention
- Full-course enrollment prevention
- Schedule conflict prevention when students enroll
- Instructor schedule conflict prevention when admins add or update courses
- Admin update protection when changing a course time would conflict with enrolled students' existing schedules
- Light/dark mode toggle
- CSV exports for students, courses, and enrollments
- PDF summary report export

## Database Tables

The required tables are implemented:

- `students(id, name, email, password_hash, salt)`
- `courses(id, name, instructor, capacity, day_of_week, start_time, end_time)`
- `enrollments(student_id, course_id)`

An additional `admins` table supports the admin login system. Student login credentials are stored on the `students` table using salted password hashes.

SQL scripts are available in `sql/` and are also packaged as runtime resources in `src/main/resources/sql/`.

## Step-By-Step Application Flow

1. `Main` applies the Swing look and feel, enables dark mode, and initializes the database.
2. `DatabaseManager` opens SQLite connections and enables foreign keys.
3. `SchemaInitializer` runs the schema script, creates the default admin, migrates student login columns if needed, and inserts sample data when the database is empty.
4. `LoginFrame` authenticates admins and students through `AuthController`.
5. Students can create an account from the Student Register tab.
6. Admin users open `MainFrame` for full management.
7. Student users open `StudentPortalFrame` to browse admin-created courses and manage their own enrollments.
8. Admins can create courses with capacity and timing.
9. Students can enroll only when seats are available and the selected course does not overlap their current schedule.
10. View panels call controllers for user actions.
11. Controllers validate input and call DAO classes.
12. DAO classes use `PreparedStatement` queries to read and update SQLite.
13. Reports are exported through `ReportController`, `CsvExporter`, and `PdfExporter`.

## Notes For Submission

Suggested report screenshots are listed in `docs/SUGGESTED_SCREENSHOTS.md`. Detailed architecture and class documentation are in `docs/DOCUMENTATION.md`.
