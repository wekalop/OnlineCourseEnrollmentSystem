INSERT OR IGNORE INTO students(id, name, email) VALUES
    (1, 'Maya Hassan', 'maya.hassan@example.com'),
    (2, 'Omar Saleh', 'omar.saleh@example.com'),
    (3, 'Lina Carter', 'lina.carter@example.com'),
    (4, 'Noah Williams', 'noah.williams@example.com'),
    (5, 'Sara Ahmed', 'sara.ahmed@example.com');

INSERT OR IGNORE INTO courses(id, name, instructor, capacity, day_of_week, start_time, end_time) VALUES
    (1, 'Java Programming Fundamentals', 'Dr. Helen Brooks', 30, 'Monday', '09:00', '10:30'),
    (2, 'Database Systems with SQLite', 'Prof. Karim Nasser', 25, 'Tuesday', '11:00', '12:30'),
    (3, 'Web Development Basics', 'Ava Martinez', 20, 'Wednesday', '13:00', '14:30'),
    (4, 'Data Visualization', 'Mona Farouk', 15, 'Thursday', '15:00', '16:30');

INSERT OR IGNORE INTO enrollments(student_id, course_id) VALUES
    (1, 1),
    (2, 1),
    (3, 2),
    (4, 3),
    (5, 4);
