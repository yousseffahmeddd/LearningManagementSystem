package com.example.demo.tests;

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

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/auth";
        userRepository.save(new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@example.com", UserRole.STUDENT, null, null , null));
    }

    @Test
    void testRegisterUserSuccess() {
        // Ensure the repository is clean before the test
        userRepository.deleteAll();

        User newUser = new User(2L, "youssef", "password", "youssef@gmail.com", UserRole.STUDENT, null, null , null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<User> request = new HttpEntity<>(newUser, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/register", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("User added successfully");
    }

    @Test
    void testLoginSuccess() {
        // Ensure the user exists in the repository with the correct password
        User existingUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.STUDENT, null, null , null);
        userRepository.save(existingUser);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>("{\"username\":\"youssef\", \"password\":\"password\"}", headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetUserProfile() {
        // Ensure the user exists in the repository with the correct email
        User existingUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.STUDENT, null, null , null);
        userRepository.save(existingUser);

        // Generate a valid token for the user
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<User> response = restTemplate.exchange(baseUrl + "/profile", HttpMethod.GET, request, User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getUsername()).isEqualTo("youssef");
        assertThat(response.getBody().getEmail()).isEqualTo("youssef@gmail.com");
    }

    @Test
    void testDeleteUser() {
        // Generate a valid token for the user
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User deleted successfully!");
    }

    @Test
    void testUpdateUser() {
        // Generate a valid token for the user
        String token = jwtService.generateToken("youssef");

        User updatedUser = new User(1L, "youssef Ahmed", "new_password", "youssefAhmed@gmail.com", UserRole.STUDENT, null, null , null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<User> request = new HttpEntity<>(updatedUser, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.PUT, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User updated successfully!");
    }

    @Test
    void testListAllUsers() {
        // Generate a valid token for the user
        String token = jwtService.generateToken("youssef");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Collection> response = restTemplate.exchange(baseUrl + "/list", HttpMethod.GET, request, Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testRegisterUserWithExistingUsernameOrEmailOrId() {
        // Ensure the user exists in the repository
        User existingUser = new User(1L, "youssef", passwordEncoder.encode("password"), "youssef@gmail.com", UserRole.STUDENT, null, null , null);
        userRepository.save(existingUser);

        // Attempt to register a new user with the same ID
        User newUserWithSameId = new User(1L, "youssef Ahmed", "new_password", "youssefAhmed@gmail.com", UserRole.STUDENT, null, null , null);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<User> requestWithSameId = new HttpEntity<>(newUserWithSameId, headers);

        ResponseEntity<String> responseWithSameId = restTemplate.postForEntity(baseUrl + "/register", requestWithSameId, String.class);
        assertThat(responseWithSameId.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(responseWithSameId.getBody()).isEqualTo("User ID already exists: 1");

        // Attempt to register a new user with the same username
        User newUserWithSameUsername = new User(2L, "youssef", "new_password", "youssefAhmed@gmail.com", UserRole.STUDENT, null, null , null);
        HttpEntity<User> requestWithSameUsername = new HttpEntity<>(newUserWithSameUsername, headers);

        ResponseEntity<String> responseWithSameUsername = restTemplate.postForEntity(baseUrl + "/register", requestWithSameUsername, String.class);
        assertThat(responseWithSameUsername.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(responseWithSameUsername.getBody()).isEqualTo("User already exists: youssef");

        // Attempt to register a new user with the same email
        User newUserWithSameEmail = new User(3L, "youssef Ahmed", "new_password", "youssef@gmail.com", UserRole.STUDENT, null, null , null);
        HttpEntity<User> requestWithSameEmail = new HttpEntity<>(newUserWithSameEmail, headers);

        ResponseEntity<String> responseWithSameEmail = restTemplate.postForEntity(baseUrl + "/register", requestWithSameEmail, String.class);
        assertThat(responseWithSameEmail.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(responseWithSameEmail.getBody()).isEqualTo("Email already exists: youssef@gmail.com");
    }
}