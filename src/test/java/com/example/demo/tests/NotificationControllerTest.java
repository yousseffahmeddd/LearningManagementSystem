package com.example.demo.tests;

import com.example.demo.models.Notification;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.repository.NotificationRepository;
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
class NotificationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;
    private String token;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/notifications";

        // Create and save a user
        User user = new User(1L, "mohamed", passwordEncoder.encode("password"), "mohamed@example.com", UserRole.STUDENT, null, null, null);
        userRepository.save(user);

        // Generate token
        token = jwtService.generateToken("mohamed");

        // Create a notification
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> createRequest = new HttpEntity<>("Test notification message", headers);
        restTemplate.postForEntity(baseUrl + "/1", createRequest, String.class);
    }

    @Test
    void testCreateNotification() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>("Another test notification message", headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/1", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Notification created successfully!");
    }

    @Test
    void testGetNotifications() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Notification[]> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.GET, request, Notification[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testGetUnreadNotifications() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Notification[]> response = restTemplate.exchange(baseUrl + "/1/unread", HttpMethod.GET, request, Notification[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void testMarkAllAsRead() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/1/readAll", HttpMethod.PUT, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("All notifications marked as read.");
    }

    @Test
    void testDeleteAllNotifications() {
        String token = jwtService.generateToken("mohamed");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> createRequest = new HttpEntity<>("Test notification message", headers);
        restTemplate.postForEntity(baseUrl + "/1", createRequest, String.class);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("All notifications deleted.");
    }

    @Test
    void testDeleteNotification() {
        String token = jwtService.generateToken("mohamed");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> createRequest = new HttpEntity<>("Test notification message", headers);
        restTemplate.postForEntity(baseUrl + "/1", createRequest, String.class);

        List<Notification> notifications = notificationRepository.findByUserId(1L);
        Long notificationId = notifications.get(0).getId();

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/1/" + notificationId, HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Notification deleted.");
    }
}