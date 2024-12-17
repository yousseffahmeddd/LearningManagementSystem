package com.example.demo.models;

public class Enrollment {

    private String courseId;
    private Long studentId;

    public Enrollment(String courseId, Long studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }

    public Enrollment() {
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "courseId='" + courseId + '\'' +
                ", studentId=" + studentId +
                '}';
    }
}
