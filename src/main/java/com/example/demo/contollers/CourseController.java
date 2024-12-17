package com.example.demo.contollers;

import com.example.demo.models.Course;
import com.example.demo.models.User;
import com.example.demo.service.CourseService;
import com.example.demo.models.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Create a new course
    @PostMapping
    public ResponseEntity<String> createCourse(
            @RequestBody Course course,
            @RequestHeader("User-Role") String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            // Call service to create course
            courseService.createCourse(userRole, course.getTitle(), course.getDescription(), course.getHours(), course.getId(), course.getInstructorId());
            return ResponseEntity.ok("Course created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    // Get all courses
    @GetMapping
    public ResponseEntity<Collection<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // Delete a course by ID
    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourse(
            @PathVariable String courseId,
            @RequestHeader("User-Role") String role) {

        try {
            // Convert role string to UserRole enum
            UserRole userRole = UserRole.valueOf(role.toUpperCase());

            // Call service to delete course
            courseService.deleteCourse(userRole, courseId);

            return ResponseEntity.ok("Course deleted successfully.");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Update an existing course
    @PutMapping("/{courseId}")
    public ResponseEntity<String> updateCourse(
            @PathVariable String courseId,
            @RequestBody Course updatedCourse,
            @RequestHeader("User-Role") String role) {

        try {
            // Convert string to UserRole enum
            UserRole userRole = UserRole.valueOf(role.toUpperCase());

            // Call the service to update the course with the entire updatedCourse object
            courseService.updateCourse(courseId, updatedCourse, userRole);

            return ResponseEntity.ok("Course updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }


}