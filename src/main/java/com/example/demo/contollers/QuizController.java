package com.example.demo.contollers;

import com.example.demo.models.Question;
import com.example.demo.models.QuizAttempt;
import com.example.demo.models.UserRole;
import com.example.demo.service.QuizService;
import com.example.demo.models.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    // Endpoint to create a new quiz
    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody Quiz quiz, @RequestHeader("User-Role") String role) {
        try {
            // Attempt to create the quiz
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            Quiz createdQuiz = quizService.createQuiz(userRole ,quiz);
            return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            // Handle duplicate title exception
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestion(@PathVariable String quizId,@RequestBody Question question,@RequestHeader("User-Role") String role) {
        try {
            // Attempt to add the question
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            quizService.addQuestion(userRole, quizId, question);
            return new ResponseEntity<>("Question added successfully.", HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            // Handle quiz not found exception
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{quizId}/attemptQuiz")
    public ResponseEntity<?> attemptQuiz(@PathVariable String quizId,@RequestParam int numOfQuestions) {
        try {
            // Attempt to add the question
            return new ResponseEntity<>(quizService.attemptQuiz(quizId, numOfQuestions), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            // Handle quiz not found exception
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{quizId}/submitQuiz")
    public ResponseEntity<?> submitQuiz(@PathVariable String quizId, @RequestBody List<Question> questions,@RequestHeader("User-Role") String role,@RequestHeader("Student-Id") String studentId) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());

            if(userRole != UserRole.STUDENT) {
                return new ResponseEntity<>("Only students can submit quizzes.", HttpStatus.FORBIDDEN);
            }

            // Retrieve the quiz by its ID
            Quiz quiz = quizService.attemptQuiz(quizId, questions.size());

            // Calculate the grade
            int correctAnswers = 0;
            for (Question submittedQuestion : questions) {
                for (Question quizQuestion : quiz.getQuestions()) {
                    if (submittedQuestion.getId().equals(quizQuestion.getId()) &&
                            submittedQuestion.getSubmittedAnswer().equals(quizQuestion.getCorrectAnswer())) {
                        correctAnswers++;
                    }
                }
            }

            // Calculate the grade as a percentage
            double grade = (double) correctAnswers / questions.size() * 100;

            QuizAttempt quizAttempt = new QuizAttempt();

            quizAttempt.setQuizId(quizId);
            quizAttempt.setStudentId(studentId);
            quizAttempt.setQuestions(questions);
            quizAttempt.setGrade(grade);
            quizService.saveQuizAttempt(quizAttempt);


            // Return the grade as a response
            return new ResponseEntity<>(grade, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            // Handle quiz not found exception
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/quizAttempts")
    public ResponseEntity<?> getQuizAttempts(@RequestHeader("User-Role") String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            if (userRole != UserRole.INSTRUCTOR) {
                return new ResponseEntity<>("Only instructors can view all quiz attempts.", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(quizService.getAllQuizAttempts(), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            // Handle student not found exception
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint to get all quizzes
    @GetMapping
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }


}
