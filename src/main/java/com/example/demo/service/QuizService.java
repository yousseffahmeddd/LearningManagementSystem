package com.example.demo.service;

import com.example.demo.models.Course;
import com.example.demo.models.Question;
import com.example.demo.models.Quiz;
import com.example.demo.models.UserRole;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuizRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Getter
@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public QuizService(QuizRepository quizRepository, CourseRepository courseRepository) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
    }

    // Method to create a quiz
    public Quiz createQuiz(UserRole role ,Quiz quiz) {
        if(!isInstructor(role)) {
            throw new IllegalArgumentException("Only Instructors can create quizzes.");
        }

        if(!courseRepository.existsById(quiz.getCourseId())) {
            throw new IllegalArgumentException("No course with ID " + quiz.getCourseId() + " exists.");
        }

        if(quizRepository.isQuizIdExist(quiz.getId())) {
            throw new IllegalArgumentException("A quiz with this ID already exists.");
        }

        return quizRepository.save(quiz);
    }

    private boolean isInstructor(UserRole role) {
        return role == UserRole.INSTRUCTOR;
    }


    public void addQuestion(UserRole role, String quizId, Question question) {
        if(!isInstructor(role)) {
            throw new IllegalArgumentException("Only Instructors can add questions to quizzes.");
        }
        if(!quizRepository.isQuizIdExist(quizId)) {
            throw new IllegalArgumentException("Quiz with ID " + quizId + " not found.");
        }
        if(questionRepository.existsById(question.getId())) {
            throw new IllegalArgumentException("Question with ID " + question.getId() + "already exists.");
        }

        Quiz quiz = quizRepository.findById(quizId);

        quiz.addQuestion(question);
        questionRepository.save(question);
        quizRepository.save(quiz);
    }



    //attempt a quiz
    public Quiz attemptQuiz(String quizId, int noOfQuestions) {
        if(!quizRepository.isQuizIdExist(quizId)) {
            throw new IllegalArgumentException("Quiz with ID " + quizId + " not found.");
        }

        Quiz quiz = quizRepository.findById(quizId);

        Course course = courseRepository.findById(quiz.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + quiz.getCourseId() + " not found."));

        quiz.randomizedQuestions(course.getCourseQuestions() ,noOfQuestions);
        return quiz;
    }

    // Get all quizzes
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
}
