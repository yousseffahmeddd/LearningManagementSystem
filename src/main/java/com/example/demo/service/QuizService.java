package com.example.demo.service;

import com.example.demo.models.Quiz;
import com.example.demo.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    // Method to create a quiz
    public Quiz createQuiz(Quiz quiz) {
        // Check if a quiz with the same title already exists
        if (quizRepository.findByTitle(quiz.getTitle()) != null) {
            throw new IllegalArgumentException("Quiz with this title already exists.");
        }
        return quizRepository.save(quiz);
    }

    // Get all quizzes
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
}
