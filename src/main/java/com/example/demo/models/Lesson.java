package com.example.demo.models;

public class Lesson {

    private Long id;
    private String courseId;
    private String title;

    public Lesson() {
    }

    public Lesson(Long id, String courseId, String title) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
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

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                '}';
    }
}
