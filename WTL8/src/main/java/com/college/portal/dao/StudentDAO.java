package com.college.portal.dao;

import com.college.portal.model.Student;
import com.college.portal.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private static final String SELECT_ALL_STUDENTS =
            "SELECT stud_id, stud_name, `class` AS student_class, division, city " +
            "FROM students_info ORDER BY stud_id";
    private static final String SELECT_STUDENT_BY_ID =
            "SELECT stud_id, stud_name, `class` AS student_class, division, city " +
            "FROM students_info WHERE stud_id = ?";
    private static final String INSERT_STUDENT =
            "INSERT INTO students_info (stud_id, stud_name, `class`, division, city) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_STUDENT =
            "UPDATE students_info SET stud_name = ?, `class` = ?, division = ?, city = ? WHERE stud_id = ?";
    private static final String DELETE_STUDENT =
            "DELETE FROM students_info WHERE stud_id = ?";

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                students.add(new Student(
                        resultSet.getInt("stud_id"),
                        resultSet.getString("stud_name"),
                        resultSet.getString("student_class"),
                        resultSet.getString("division"),
                        resultSet.getString("city")
                ));
            }
        }

        return students;
    }

    public Student getStudentById(int studId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENT_BY_ID)) {

            preparedStatement.setInt(1, studId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Student(
                            resultSet.getInt("stud_id"),
                            resultSet.getString("stud_name"),
                            resultSet.getString("student_class"),
                            resultSet.getString("division"),
                            resultSet.getString("city")
                    );
                }
            }
        }

        return null;
    }

    public void addStudent(Student student) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT)) {

            preparedStatement.setInt(1, student.getStudId());
            preparedStatement.setString(2, student.getStudName());
            preparedStatement.setString(3, student.getStudentClass());
            preparedStatement.setString(4, student.getDivision());
            preparedStatement.setString(5, student.getCity());
            preparedStatement.executeUpdate();
        }
    }

    public boolean updateStudent(Student student) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT)) {

            preparedStatement.setString(1, student.getStudName());
            preparedStatement.setString(2, student.getStudentClass());
            preparedStatement.setString(3, student.getDivision());
            preparedStatement.setString(4, student.getCity());
            preparedStatement.setInt(5, student.getStudId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteStudent(int studId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STUDENT)) {

            preparedStatement.setInt(1, studId);
            return preparedStatement.executeUpdate() > 0;
        }
    }
}
