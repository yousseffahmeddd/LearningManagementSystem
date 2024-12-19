package com.example.demo.models;

import java.util.List;

public class Question {
    private String text;
    private QuestionType type;
    private List<String> options; // Only applicable for MCQ
    private String correctAnswer; // For True/False or Short Answer

    // Constructor
    public Question(String text, QuestionType type, List<String> options, String correctAnswer) {
        this.text = text;
        this.type = type;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
