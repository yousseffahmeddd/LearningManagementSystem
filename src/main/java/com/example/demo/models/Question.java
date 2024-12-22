package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Question {
    // Getters and Setters
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

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                ", type=" + type +
                ", options=" + options +
                ", correctAnswer='" + correctAnswer + '\'' +
                '}';
    }
}
