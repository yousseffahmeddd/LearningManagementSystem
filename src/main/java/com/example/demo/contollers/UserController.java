package com.example.demo.contollers;

import com.example.demo.models.Course;
import com.example.demo.models.Notification;
import com.example.demo.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.demo.models.AuthRequest;
import com.example.demo.service.JWTService;
import com.example.demo.service.UserService;
import java.util.Collection;
import java.util.List;

import com.example.demo.models.User;
@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService service;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final NotificationService notificationService;

    public UserController(UserService service, JWTService jwtService, AuthenticationManager authenticationManager, NotificationService notificationService) {
        this.service = service;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.notificationService = notificationService;
    }

    // src/main/java/com/example/demo/contollers/UserController.java
    @PostMapping("/register")
    public ResponseEntity<String> addNewUser(@RequestBody User userInfo) {
        if (service.userExists(userInfo.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists: " + userInfo.getUsername());
        }
        if (service.userIdExists(userInfo.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User ID already exists: " + userInfo.getId());
        }
        if (service.userEmailExists(userInfo.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists: " + userInfo.getEmail());
        }
        String response = service.addUser(userInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        /*if (!service.userExists(authRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + authRequest.getUsername());
        }*/
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authRequest.getUsername());
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password!");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<User> profile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = service.getUserProfile(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listAllUsers() {
        Collection<User> users = service.listAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.ok("Please enter users to list");
        }
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            service.updateUser(id, user);
            return ResponseEntity.ok("User updated successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, World!");
    }


    @GetMapping("/{studentId}/courses")
    public ResponseEntity<List<Course>> getEnrolledCourses(@PathVariable Long studentId) {
        List<Course> courses = service.getEnrolledCourses(studentId);
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/{userId}/notifications")
    public ResponseEntity<String> createNotification(@PathVariable Long userId, @RequestBody String message) {
        notificationService.createNotification(userId, message);
        return ResponseEntity.ok("Notification created successfully!");
    }

    @GetMapping("/{userId}/notifications")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{userId}/notifications/{notificationId}/read")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long userId, @PathVariable Long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.ok("Notification marked as read.");
    }

    @DeleteMapping("/{userId}/notifications/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long userId, @PathVariable Long notificationId) {
        notificationService.deleteNotification(userId, notificationId);
        return ResponseEntity.ok("Notification deleted.");
    }

    @DeleteMapping("/{userId}/notifications")
    public ResponseEntity<String> deleteAllNotifications(@PathVariable Long userId) {
        notificationService.deleteAllNotifications(userId);
        return ResponseEntity.ok("All notifications deleted.");
    }
}