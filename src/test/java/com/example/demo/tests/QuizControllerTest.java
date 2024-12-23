package com.example.demo.tests;

import com.example.demo.models.Question;
import com.example.demo.models.QuestionType;
import com.example.demo.models.Quiz;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuizControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/quizzes";

        // Create and save the required users
        User instructorUser = new User(1L, "instructor_user", passwordEncoder.encode("password"), "instructor@example.com", UserRole.INSTRUCTOR, null, null);
        User studentUser = new User(2L, "student_user", passwordEncoder.encode("password"), "student@example.com", UserRole.STUDENT, null, null);
        userRepository.save(instructorUser);
        userRepository.save(studentUser);
    }

    @Test
    void testCreateQuiz() {
        String token = jwtService.generateToken("instructor_user");

        Quiz newQuiz = new Quiz("1", "1", "Sample Quiz");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Quiz> request = new HttpEntity<>(newQuiz, headers);

        ResponseEntity<Quiz> response = restTemplate.postForEntity(baseUrl, request, Quiz.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Sample Quiz");
    }

    @Test
    void testAddQuestion() {
        String token = jwtService.generateToken("instructor_user");

        Question newQuestion = new Question("1", "Sample Question", QuestionType.MCQ, List.of("Option 1", "Option 2"), "Option 1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Question> request = new HttpEntity<>(newQuestion, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/1/questions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Question added successfully.");
    }

    @Test
    void testAttemptQuiz() {
        String token = jwtService.generateToken("student_user");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Quiz> response = restTemplate.exchange(baseUrl + "/1/attemptQuiz?numOfQuestions=1", HttpMethod.POST, request, Quiz.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRandomizedQuestions()).hasSize(1);
    }

    @Test
    void testSubmitQuiz() {
        String token = jwtService.generateToken("student_user");

        Question submittedQuestion = new Question("1", "Sample Question", QuestionType.MCQ, List.of("Option 1", "Option 2"), "Option 1");
        submittedQuestion.setSubmittedAnswer("Option 1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<List<Question>> request = new HttpEntity<>(List.of(submittedQuestion), headers);

        ResponseEntity<Double> response = restTemplate.postForEntity(baseUrl + "/1/submitQuiz", request, Double.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(100.0);
    }
}