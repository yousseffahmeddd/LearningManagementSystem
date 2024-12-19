package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Lesson {

    private Long id;
    private String courseId;
    private String title;
    private Long instructorId;

    public Lesson() {
    }

    public Lesson(Long id, String courseId, String title, Long instructorId) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.instructorId = instructorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", courseId='" + courseId + '\'' +
                ", title='" + title + '\'' +
                ", instructorId=" + instructorId +
                '}';
    }
}
