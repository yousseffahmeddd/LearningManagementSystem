package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Course {
    private String id;
    private String title;
    private String description;
    private Integer hours;
    private Long instructorId;

    @JsonIgnore
    private List<User> students;

    @JsonIgnore
    private List<Lesson> lessons;


    public Course() {
        this.students = new ArrayList<>();
        this.lessons = new ArrayList<>();
    }

    public Course(String id, String title, String description, Integer hours, Long instructorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.hours = hours;
        this.instructorId = instructorId;
        this.students = new ArrayList<>();
        this.lessons = new ArrayList<>();
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

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @JsonProperty("studentCount")
    public int getStudentCount() {
        return students.size();
    }

    @JsonProperty("studentIds")
    public List<Long> getStudentIds() {
        return students.stream().map(User::getId).collect(Collectors.toList());
    }

    @JsonProperty("LessonCount")
    public int LessonCount() {
        return lessons.size();
    }

    @JsonProperty("lessonIds")
    public List<Long> getLessonIds() {
        return lessons.stream().map(Lesson::getId).collect(Collectors.toList());
    }



    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", hours=" + hours +
                ", instructorId=" + instructorId +
                '}';
    }
}