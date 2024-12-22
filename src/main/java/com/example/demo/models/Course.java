package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Course {
    private String id;
    private String title;
    private String description;
    private Integer hours;
    private Long instructorId;
    private List<Question> courseQuestions;

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
        this.courseQuestions = new ArrayList<>();
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

    public void addQuestionToCourseQuestions(Question question) {
        this.courseQuestions.add(question);
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