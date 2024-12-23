package com.example.demo.models;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.atomic.AtomicLong;

public class Attendance {

    @JsonIgnore
    private Long id;
    private Long lessonId;
    private Long studentId;
    private boolean isMarked;
    private String otp;
    private Lesson lesson;

    public Attendance() {
    }

    public Attendance(Lesson lesson, User student, String otp) {
        this.lesson = lesson;
        this.lessonId = lesson.getId();
        this.studentId = null; // <-- Allow it to be assigned when a real student submits OTP
        this.otp = otp;
        this.isMarked = false;
    }

    public Long getId() {
        return id;
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

    public Lesson getLesson() { // Add this getter method
        return lesson;
    }

    public void setLesson(Lesson lesson) { // Add this setter method
        this.lesson = lesson;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", lessonId=" + lessonId +
                ", studentId=" + studentId +
                ", isMarked=" + isMarked +
                ", otp='" + otp + '\'' +
                ", lesson=" + lesson +
                '}';
    }
}