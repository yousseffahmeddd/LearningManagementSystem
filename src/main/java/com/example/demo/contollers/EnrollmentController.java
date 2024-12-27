package com.example.demo.contollers;

import com.example.demo.models.Enrollment;
import com.example.demo.models.UserRole;
import com.example.demo.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }


    @PostMapping
    public ResponseEntity<String> enrollStudent(@RequestBody Enrollment enrollment,
                                                @RequestHeader("User-Role") String role) {
        try {
            // Call the service to handle enrollment and role validation
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            enrollmentService.enrollStudent(enrollment.getCourseId(), enrollment.getStudentId() ,userRole );
            return ResponseEntity.ok("Student enrolled successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> listAllEnrollments(
            @RequestHeader("User-Role") String role
    ) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            return ResponseEntity.ok(enrollmentService.getAllEnrollments(userRole));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }


    @GetMapping("/course/{courseId}")
    public ResponseEntity<Collection<Enrollment>> listEnrollmentsByCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourseId(courseId));
    }
}