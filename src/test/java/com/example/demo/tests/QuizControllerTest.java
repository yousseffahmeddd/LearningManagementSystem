package com.example.demo.tests;

import com.example.demo.models.*;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.QuizRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JWTService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
    private CourseRepository courseRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/quizzes";

        // Create and save the required users
        User instructorUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.INSTRUCTOR, null, null);
        User studentUser = new User(2L, "mazen", passwordEncoder.encode("password"), "mazen@gmail.com", UserRole.STUDENT, null, null);
        User adminUser = new User(3L, "mohamed", passwordEncoder.encode("password"), "mohamed@gmail.com", UserRole.ADMIN, null, null);
        userRepository.save(instructorUser);
        userRepository.save(studentUser);
        userRepository.save(adminUser);
    }

    @Test
    void testCreateQuizAsInstructor() {
        // Create a course first
        Course existingCourse = new Course("CS001", "C++ Programming", "Introduction to C++ Programming", 3, 1L);
        courseRepository.save(existingCourse);

        // Now create a quiz
        String token = jwtService.generateToken("youssef");
        Quiz newQuiz = new Quiz("1", "CS001", "Sample Quiz");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Quiz> request = new HttpEntity<>(newQuiz, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Quiz createdQuiz = objectMapper.readValue(response.getBody(), Quiz.class);
                assertThat(createdQuiz).isNotNull();
                assertThat(createdQuiz.getTitle()).isEqualTo("Sample Quiz");
            } catch (JsonProcessingException e) {
                fail("Failed to parse response body as Quiz");
            }
        } else {
            System.out.println("Error response: " + response.getBody());
            fail("Expected status code 201 CREATED but got " + response.getStatusCode());
        }
    }

    @Test
    void testCreateQuizAsAdmin() {
        String token = jwtService.generateToken("mohamed");

        Quiz newQuiz = new Quiz("2", "CS002", "Sample Quiz 2");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Quiz> request = new HttpEntity<>(newQuiz, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Only Instructors can create quizzes.");
    }

    @Test
    void testCreateQuizAsStudent() {
        String token = jwtService.generateToken("mazen");

        Quiz newQuiz = new Quiz("3", "CS003", "Sample Quiz 3");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Quiz> request = new HttpEntity<>(newQuiz, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Only Instructors can create quizzes.");
    }

    @Test
    void testAddQuestionAsInstructor() {
        String token = jwtService.generateToken("youssef");

        // Create a course first
        Course existingCourse = new Course("CS001", "C++ Programming", "Introduction to C++ Programming", 3, 1L);
        courseRepository.save(existingCourse);

        // Create a quiz
        Quiz newQuiz = new Quiz("1", "CS001", "Sample Quiz");
        quizRepository.save(newQuiz);

        // Now add a question to the quiz
        Question newQuestion = new Question("1", "Sample Question", QuestionType.MCQ, List.of("Option 1", "Option 2"), "Option 1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Question> request = new HttpEntity<>(newQuestion, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/1/questions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Question added successfully.");
    }

    @Test
    void testAddQuestionAsStudent() {
        String token = jwtService.generateToken("mazen");

        Question newQuestion = new Question("2", "Sample Question 2", QuestionType.MCQ, List.of("Option 1", "Option 2"), "Option 1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Question> request = new HttpEntity<>(newQuestion, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/1/questions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Only Instructors can add questions to quizzes.");
    }

    @Test
    void testSubmitQuizAsStudent() {
        String token = jwtService.generateToken("mazen");

        // Ensure the course and quiz exist
        Course existingCourse = new Course("CS001", "C++ Programming", "Introduction to C++ Programming", 3, 1L);
        courseRepository.save(existingCourse);
        Quiz existingQuiz = new Quiz("1", "CS001", "Sample Quiz");
        existingQuiz.addQuestion(new Question("1", "Sample Question", QuestionType.MCQ, List.of("Option 1", "Option 2"), "Option 1"));
        quizRepository.save(existingQuiz);

        Question submittedQuestion = new Question("1", "Sample Question", QuestionType.MCQ, List.of("Option 1", "Option 2"), "Option 1");
        submittedQuestion.setSubmittedAnswer("Option 1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Student-Id", "2");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<List<Question>> request = new HttpEntity<>(List.of(submittedQuestion), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/1/submitQuiz", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testSubmitQuizAsInstructor() {
        String token = jwtService.generateToken("youssef");

        Question submittedQuestion = new Question("1", "Sample Question", QuestionType.MCQ, List.of("Option 1", "Option 2"), "Option 1");
        submittedQuestion.setSubmittedAnswer("Option 1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Student-Id", "1");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<List<Question>> request = new HttpEntity<>(List.of(submittedQuestion), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/1/submitQuiz", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("Only students can submit quizzes.");
    }


    @Test
    void testAttemptQuiz() {
        String token = jwtService.generateToken("mazen");

        // Create a course first
        Course existingCourse = new Course("CS001", "C++ Programming", "Introduction to C++ Programming", 3, 1L);
        courseRepository.save(existingCourse);

        // Create a quiz
        Quiz newQuiz = new Quiz("1", "CS001", "Sample Quiz");
        quizRepository.save(newQuiz);

        // Add a question to the quiz
        Question newQuestion = new Question("1", "Sample Question", QuestionType.MCQ, List.of("Option 1", "Option 2"), "Option 1");
        newQuiz.addQuestion(newQuestion);
        quizRepository.save(newQuiz);

        // Attempt the quiz
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/1/attemptQuiz?numOfQuestions=1", HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            assertThat(response.getBody()).isNotNull();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleModule module = new SimpleModule();
                module.addDeserializer(Question.class, new JsonDeserializer<Question>() {
                    @Override
                    public Question deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        JsonNode node = p.getCodec().readTree(p);
                        String id = node.get("id").asText();
                        String text = node.get("text").asText();
                        QuestionType type = QuestionType.valueOf(node.get("type").asText());
                        List<String> options = new ArrayList<>();
                        node.get("options").forEach(option -> options.add(option.asText()));
                        String correctAnswer = node.get("correctAnswer").asText();
                        return new Question(id, text, type, options, correctAnswer);
                    }
                });
                objectMapper.registerModule(module);

                Quiz quiz = objectMapper.readValue(response.getBody(), Quiz.class);
                assertThat(quiz.randomizedQuestions(quiz.getQuestions(), 1)).hasSize(1);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                fail("Failed to parse response body to Quiz object");
            }
        } else {
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    void testGetAllQuizzes() {
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Quiz[]> response = restTemplate.exchange(baseUrl, HttpMethod.GET, request, Quiz[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testGetQuizAttemptsAsInstructor() {
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/quizAttempts", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testGetQuizAttemptsAsStudent() {
        String token = jwtService.generateToken("mazen");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/quizAttempts", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("Only instructors can view all quiz attempts.");
    }

    @Test
    void testGetQuizAttemptsByStudent() {
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/quizAttempts/2", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }


    @Test
    void testSubmitQuizAsAdmin() {
        String token = jwtService.generateToken("mohamed");

        Question submittedQuestion = new Question("1", "Sample Question", QuestionType.MCQ, List.of("Option 1", "Option 2"), "Option 1");
        submittedQuestion.setSubmittedAnswer("Option 1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Student-Id", "3");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<List<Question>> request = new HttpEntity<>(List.of(submittedQuestion), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/1/submitQuiz", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("Only students can submit quizzes.");
    }

    @Test
    void testGetQuizAttemptsAsAdmin() {
        String token = jwtService.generateToken("mohamed");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/quizAttempts", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("Only instructors can view all quiz attempts.");
    }

    @Test
    void testAddQuestionAsAdmin() {
        String token = jwtService.generateToken("mohamed");

        Question newQuestion = new Question("2", "Sample Question 2", QuestionType.MCQ, List.of("Option 1", "Option 2"), "Option 1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "ADMIN");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Question> request = new HttpEntity<>(newQuestion, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/1/questions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Only Instructors can add questions to quizzes.");
    }
}