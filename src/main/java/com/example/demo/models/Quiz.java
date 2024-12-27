package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
public class Quiz {
    // Getters and Setters
    private String id;
    private String courseId;
    private String title;
    @JsonIgnore
    private List<Question> questions;
    private List<Question> randomizedQuestions;

    public Quiz () {
        this.questions = new ArrayList<>();
    }

    // Constructor
    public Quiz(String id ,String courseId,String title) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.questions = new ArrayList<>();
    }

    public void randomizedQuestions(int numberOfQuestions) {
        Collections.shuffle(questions); // Randomize the list
        this.randomizedQuestions = questions.subList(0, Math.min(numberOfQuestions, questions.size()));

    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

}