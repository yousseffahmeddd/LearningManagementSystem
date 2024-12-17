package com.example.demo.models;

import java.util.List;

public class Course {
    private String id;
    private String title;
    private String description;
    private Integer hours;
    private Long instructorId;
    private List<User> students;

    public Course() {
    }

    public Course(String id, String title, String description, Integer hours, Long instructorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.hours = hours;
        this.instructorId = instructorId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructor(Long instructor) {
        this.instructorId = instructor;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", hours=" + hours +
                ", instructor=" + instructorId +
                ", students=" + students +
                '}';
    }
}