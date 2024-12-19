package com.example.demo.repository;

import com.example.demo.models.Quiz;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class QuizRepository {

    private final ConcurrentHashMap<String, Quiz> quizzes = new ConcurrentHashMap<>();

    // Save a quiz
    public Quiz save(Quiz quiz) {
        quizzes.put(quiz.getTitle(), quiz);
        return quiz;
    }

    // Find a quiz by title
    public Quiz findByTitle(String title) {
        return quizzes.get(title);
    }

    // Get all quizzes
    public List<Quiz> findAll() {
        return new ArrayList<>(quizzes.values());
    }
}
