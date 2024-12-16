package com.example.demo.service;
import com.example.demo.models.UserRole;
import com.example.demo.models.Course;
import com.example.demo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository repository ;

    @Autowired
    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }

    public Course createCourse(UserRole role, String title, String description, Integer hours, String id) {
        if (!isInstructorOrAdmin(role)) {
            throw new IllegalArgumentException("Only Instructors or Admins can create courses.");
        }

        if (repository.existsById(id)) {
            throw new IllegalArgumentException("A course with this ID already exists.");
        }
        if (repository.existsByTitle(title)) {
            throw new IllegalArgumentException("A course with this title already exists.");
        }

        return repository.save(id, title, description, hours);
    }

    public Collection<Course> getAllCourses() {
        return repository.findAll();
    }

    public void deleteCourse(UserRole role, String courseId) {
        if (!isAdmin(role)) {
            throw new IllegalArgumentException("Only Admins can delete courses.");
        }

        // Check if the course exists before deleting
        if (!repository.existsById(courseId)) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found.");
        }
        repository.delete(courseId);
    }

    // Update course data
    public void updateCourse(String id, Course updatedCourse, UserRole role) {
        // Check if the user has the right role (Instructor or Admin)
        if (!isInstructorOrAdmin(role)) {
            throw new IllegalArgumentException("Only Instructors or Admins can update courses.");
        }

        // Check if the course exists for the given ID
        Optional<Course> existingCourse = repository.findById(id);
        if (existingCourse.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
        }

        // Check for duplicate title (only if the title is being changed)
        if (repository.existsByTitle(updatedCourse.getTitle())
                && !existingCourse.get().getTitle().equalsIgnoreCase(updatedCourse.getTitle())) {
            throw new IllegalArgumentException("A course with the title '" + updatedCourse.getTitle() + "' already exists.");
        }

        // Check for duplicate ID (if the ID is being changed, but usually it shouldn't)
        if (repository.existsById(updatedCourse.getId()) && !id.equals(updatedCourse.getId())) {
            throw new IllegalArgumentException("Another course with the ID " + updatedCourse.getId() + " already exists.");
        }

        // Update the course
        repository.delete(id);  // Delete the existing course by its ID
        repository.save(updatedCourse.getId(), updatedCourse.getTitle(), updatedCourse.getDescription(), updatedCourse.getHours());  // Save the updated course
    }

    private boolean isInstructorOrAdmin(UserRole role) {
        return role == UserRole.INSTRUCTOR || role == UserRole.ADMIN;
    }

    private boolean isAdmin(UserRole role) {
        return role == UserRole.ADMIN;
    }
}
