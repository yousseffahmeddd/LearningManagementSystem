package com.example.demo.repository;

import com.example.demo.models.Quiz;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class QuizRepository {

    private final HashMap<String, Quiz> quizzes = new HashMap<>();

    public Quiz save(String id, String title, String courseId) {
        Quiz quiz = new Quiz(id, courseId, title);
        quizzes.put(quiz.getId(), quiz);
        return quiz;
    }

    // Save a quiz
    public Quiz save(Quiz quiz) {
        quizzes.put(quiz.getId(), quiz);
        return quiz;
    }


    public Quiz findById(String id) {
        return quizzes.get(id);
    }


    public boolean isQuizIdExist (String id) {
        return quizzes.containsKey(id);
    }

    // Get all quizzes
    public List<Quiz> findAll() {
        return new ArrayList<>(quizzes.values());
    }

    public void delete(String id) {
        quizzes.remove(id);
    }
}
