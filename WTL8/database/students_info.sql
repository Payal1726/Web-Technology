CREATE DATABASE IF NOT EXISTS college_portal;
USE college_portal;

DROP TABLE IF EXISTS students_info;

CREATE TABLE students_info (
    stud_id INT PRIMARY KEY,
    stud_name VARCHAR(100) NOT NULL,
    `class` VARCHAR(20) NOT NULL,
    division VARCHAR(10) NOT NULL,
    city VARCHAR(100) NOT NULL
);

INSERT INTO students_info (stud_id, stud_name, `class`, division, city) VALUES
    (101, 'Aarav Shah', 'FYBSc', 'A', 'Mumbai'),
    (102, 'Diya Patel', 'SYBCom', 'B', 'Pune'),
    (103, 'Rohan Mehta', 'TYBCA', 'A', 'Nashik'),
    (104, 'Sneha Kulkarni', 'FYBA', 'C', 'Nagpur'),
    (105, 'Kabir Joshi', 'SYBBA', 'B', 'Aurangabad');

SELECT stud_id, stud_name, `class`, division, city
FROM students_info
ORDER BY stud_id;
