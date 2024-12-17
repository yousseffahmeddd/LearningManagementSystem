package com.example.demo.repository;

import com.example.demo.models.Enrollment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EnrollmentRepository {

    private final List<Enrollment> enrollments = new ArrayList<>();
    // Save an enrollment
    public void save(String courseId, Long studentId) {
        Enrollment enrollment = new Enrollment( courseId, studentId);
        enrollments.add(enrollment);
    }

    // Get all enrollments
    public List<Enrollment> findAll() {
        return new ArrayList<>(enrollments); // Return a copy to prevent modification
    }

    // Check if a student is already enrolled in a course
    public boolean existsByCourseIdAndStudentId(String courseId, Long studentId) {
        return enrollments.stream()
                .anyMatch(enrollment -> enrollment.getCourseId().equals(courseId) &&
                        enrollment.getStudentId().equals(studentId));
    }

    // Find all enrollments by course ID
    public List<Enrollment> findByCourseId(String courseId) {
        return enrollments.stream()
                .filter(enrollment -> enrollment.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }
}