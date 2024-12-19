package com.example.demo.models;

import java.util.List;

public class Quiz {
    private String title;
    private List<Question> questions;

    // Constructor
    public Quiz(String title, List<Question> questions) {
        this.title = title;
        this.questions = questions;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
