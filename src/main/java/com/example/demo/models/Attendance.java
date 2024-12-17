package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Attendance {
    private Long lessonId;
    private Long studentId;
    private boolean isMarked;
    private String otp;


    public Attendance(Long lessonId, Long studentId, String otp) {
        this.lessonId = lessonId;
        this.studentId = studentId;
        this.otp = otp;
        this.isMarked = false;
    }

    public Attendance() {
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "lessonId=" + lessonId +
                ", studentId='" + studentId + '\'' +
                ", isMarked=" + isMarked +
                '}';
    }
}
