CREATE DATABASE quiz_app;
USE quiz_app;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('USER','ADMIN') DEFAULT 'USER'
);

CREATE TABLE sections (
    section_id INT AUTO_INCREMENT PRIMARY KEY,
    section_name VARCHAR(100) NOT NULL
);

CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    section_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(200),
    option_b VARCHAR(200),
    option_c VARCHAR(200),
    option_d VARCHAR(200),
    correct_option CHAR(1),
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE
);

CREATE TABLE quiz_attempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    section_id INT NOT NULL,
    score INT DEFAULT 0,
    attempt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE
);

CREATE TABLE attempt_answers (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option CHAR(1),
    is_correct BOOLEAN,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);

INSERT INTO sections (section_name) VALUES ('Java'), ('SQL'), ('HTML & CSS');

INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'ADMIN'),
('akash', 'akash123', 'USER');

-- Insert Java Questions
INSERT INTO questions (section_id, question_text, option_a, option_b, option_c, option_d, correct_option)
VALUES
(1, 'Which keyword is used to inherit a class in Java?', 'extends', 'implements', 'inherit', 'super', 'A'),
(1, 'Which method is the entry point of a Java program?', 'main()', 'start()', 'run()', 'init()', 'A'),
(1, 'Which collection does not allow duplicates?', 'List', 'Set', 'Map', 'Queue', 'B'),
(1, 'Which OOP principle is related to method overloading?', 'Inheritance', 'Polymorphism', 'Abstraction', 'Encapsulation', 'B'),
(1, 'Which package is automatically imported in Java?', 'java.lang', 'java.util', 'java.io', 'java.sql', 'A'),
(1, 'What is JVM full form?', 'Java Virtual Machine', 'Java Variable Manager', 'Java Verified Method', 'Java Visual Module', 'A'),
(1, 'Which access modifier allows visibility everywhere?', 'private', 'protected', 'public', 'default', 'C');

-- Insert SQL Questions
INSERT INTO questions (section_id, question_text, option_a, option_b, option_c, option_d, correct_option)
VALUES
(2, 'Which SQL command is used to fetch data?', 'INSERT', 'UPDATE', 'SELECT', 'DELETE', 'C'),
(2, 'Which SQL clause is used to filter rows?', 'WHERE', 'GROUP BY', 'ORDER BY', 'HAVING', 'A'),
(2, 'Which key uniquely identifies a row in a table?', 'Foreign Key', 'Primary Key', 'Unique Key', 'Composite Key', 'B'),
(2, 'Which SQL function returns total rows?', 'SUM()', 'COUNT()', 'MAX()', 'MIN()', 'B'),
(2, 'Which join returns all rows from both tables?', 'INNER JOIN', 'LEFT JOIN', 'FULL OUTER JOIN', 'RIGHT JOIN', 'C'),
(2, 'Which statement is correct to remove a table?', 'DROP TABLE table_name', 'DELETE TABLE table_name', 'REMOVE TABLE table_name', 'TRUNCATE TABLE table_name', 'A'),
(2, 'Which constraint ensures no NULL values?', 'NOT NULL', 'PRIMARY KEY', 'UNIQUE', 'FOREIGN KEY', 'A');

-- Insert HTML & CSS Questions
INSERT INTO questions (section_id, question_text, option_a, option_b, option_c, option_d, correct_option)
VALUES
(3, 'Which tag is used for the largest heading in HTML?', '<h1>', '<h6>', '<head>', '<header>', 'A'),
(3, 'Which attribute sets the background color in CSS?', 'background', 'bgcolor', 'color', 'background-color', 'D'),
(3, 'Which tag creates a hyperlink in HTML?', '<a>', '<link>', '<href>', '<url>', 'A'),
(3, 'Which CSS property controls text size?', 'font-size', 'text-size', 'size', 'font', 'A'),
(3, 'Which input type is used for passwords?', '<input type="password">', '<input type="text">', '<input type="hidden">', '<input type="secure">', 'A'),
(3, 'Which HTML element is used for line breaks?', '<br>', '<break>', '<lb>', '<hr>', 'A'),
(3, 'Which CSS property makes text bold?', 'text-bold', 'font-weight', 'bold', 'font-style', 'B');
