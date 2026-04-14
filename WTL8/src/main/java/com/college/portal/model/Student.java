package com.college.portal.model;

public class Student {

    private final int studId;
    private final String studName;
    private final String studentClass;
    private final String division;
    private final String city;

    public Student(int studId, String studName, String studentClass, String division, String city) {
        this.studId = studId;
        this.studName = studName;
        this.studentClass = studentClass;
        this.division = division;
        this.city = city;
    }

    public int getStudId() {
        return studId;
    }

    public String getStudName() {
        return studName;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public String getDivision() {
        return division;
    }

    public String getCity() {
        return city;
    }
}
