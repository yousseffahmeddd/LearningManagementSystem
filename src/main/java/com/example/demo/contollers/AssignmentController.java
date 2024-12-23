package com.example.demo.contollers;

import com.example.demo.models.Assignment;
import com.example.demo.models.Quiz;
import com.example.demo.models.Submission;
import com.example.demo.models.UserRole;
import com.example.demo.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    private  AssignmentService assignmentService;

    @Autowired
    private AssignmentService AssignmentService;


    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createAssignment(
            @RequestHeader("User-Role") String userRole,
            @RequestBody Assignment assignmentRequest) {
        try {
            UserRole role = UserRole.valueOf(userRole.toUpperCase());

            // Check if the role is valid (Only ADMIN or INSTRUCTOR can create assignments)
            if (role != UserRole.ADMIN && role != UserRole.INSTRUCTOR) {
                return ResponseEntity.status(400).body(
                        Map.of("error", "Only instructors or admins can create assignments.")
                );
            }

            // Valid role; proceed to create the assignment
            Assignment assignment = assignmentService.createAssignment(
                    assignmentRequest.getId(),
                    assignmentRequest.getTitle(),
                    assignmentRequest.getDescription(),
                    assignmentRequest.getCourseId(),
                    role
            );
            return ResponseEntity.ok(assignment);

        } catch (IllegalArgumentException e) {
            // Handle invalid roles gracefully
            return ResponseEntity.status(400).body(
                    Map.of("error", "Invalid User Role provided.")
            );
        }
    }



    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<Object> submitAssignment(
            @RequestHeader("Student-Id") String studentId,
            @RequestHeader("User-Role") String userRole,
            @PathVariable String assignmentId,
            @RequestParam("file") MultipartFile file) {

        try {
            if (!userRole.equalsIgnoreCase("STUDENT")) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Only students are allowed to submit assignments.")
                );
            }

            // Use the submitAssignment method directly for handling the submission
            Submission submission = assignmentService.submitAssignment(studentId, assignmentId, file);

            return ResponseEntity.ok(submission);

        } catch (IllegalArgumentException e) {
            // Handle other possible errors
            return ResponseEntity.status(400).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
    @GetMapping("/assignments")
    public List<Assignment> getAllAssignments() {
        return AssignmentService.getAllassignments();
    }

    @GetMapping("/submissions")
    public ResponseEntity<Object> getAllSubmissions(@RequestHeader("User-Role") String userRole) {
        try {
            UserRole role = UserRole.valueOf(userRole.toUpperCase());

            // Restrict access to only instructors
            if (role != UserRole.INSTRUCTOR) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Access denied: Only instructors can view all submissions.")
                );
            }

            // Fetch submissions
            List<Submission> submissions = assignmentService.getAllSubmissions(role);
            return ResponseEntity.ok(submissions);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(
                    Map.of("error", "Invalid User Role provided.")
            );
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}