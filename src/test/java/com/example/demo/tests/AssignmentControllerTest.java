package com.example.demo.tests;

import com.example.demo.models.Assignment;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.AssignmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.IOException;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AssignmentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/assignments";

        // Create and save the required users
        User instructorUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.INSTRUCTOR, null, null);
        User studentUser = new User(2L, "mazen", passwordEncoder.encode("password"), "mazen@gmail.com", UserRole.STUDENT, null, null);
        userRepository.save(instructorUser);
        userRepository.save(studentUser);
    }

    @Test
    void testCreateAssignment() {
        String token = jwtService.generateToken("youssef");

        Assignment newAssignment = new Assignment("1", "Sample Assignment", "Description", "1", null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Assignment> request = new HttpEntity<>(newAssignment, headers);

        ResponseEntity<Assignment> response = restTemplate.postForEntity(baseUrl + "/create", request, Assignment.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Sample Assignment");
    }

    @Test
    void testSubmitAssignment() {
        String token = jwtService.generateToken("mazen");

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Student-Id", "2");
        headers.set("User-Role", "STUDENT");
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/1/submit", HttpMethod.POST, requestEntity, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("test.txt");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException occurred while getting file bytes");
        }
    }

    @Test
    void testAddGradeAndFeedback() {
        String token = jwtService.generateToken("youssef");

        // Ensure the assignment exists
        Assignment newAssignment = new Assignment("1", "Sample Assignment", "Description", "1", null, null);
        assignmentRepository.save(newAssignment);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Role", "INSTRUCTOR");
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("grade", "A", "feedback", "Good job"), headers);

        ResponseEntity<Assignment> response = restTemplate.postForEntity(baseUrl + "/1/feedback", request, Assignment.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGrade()).isEqualTo("A");
        assertThat(response.getBody().getFeedback()).isEqualTo("Good job");
    }
}