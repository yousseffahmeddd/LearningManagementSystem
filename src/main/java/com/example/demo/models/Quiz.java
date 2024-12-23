package com.example.demo.models;

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

    public List<Question> randomizedQuestions(List<Question> questionBank, int numberOfQuestions) {
        Collections.shuffle(questionBank); // Randomize the list
        this.randomizedQuestions = questionBank.subList(0, Math.min(numberOfQuestions, questionBank.size()));
        return this.randomizedQuestions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

}
