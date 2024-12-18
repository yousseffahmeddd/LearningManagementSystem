package com.example.demo.service;
import com.example.demo.models.CourseDTO;
import com.example.demo.models.User;
import com.example.demo.models.UserRole;
import com.example.demo.models.Course;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Course createCourse(UserRole role, String title, String description, Integer hours, String courseId, Long instructorId) {
        if (!isInstructorOrAdmin(role)) {
            throw new IllegalArgumentException("Only Instructors or Admins can create courses.");
        }

        if (courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("A course with this ID already exists.");
        }
        if (courseRepository.existsByTitle(title)) {
            throw new IllegalArgumentException("A course with this title already exists.");
        }

        // Check if the instructor exists
        Optional<User> instructorOptional = userRepository.findById(instructorId);
        if (instructorOptional.isEmpty() || instructorOptional.get().getRole() != UserRole.INSTRUCTOR) {
            throw new IllegalArgumentException("Instructor not found or not an instructor.");
        }

        User instructor = instructorOptional.get();
        Course course = new Course(courseId, title, description, hours, instructorId);

        // Convert Course to CourseDTO and add it to the instructor's list of courses
        CourseDTO courseDTO = new CourseDTO(course.getId(), course.getTitle());
        instructor.getCourses().add(courseDTO);
        userRepository.save(instructor);

        return courseRepository.save(course);
    }

    public Collection<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public void deleteCourse(UserRole role, String courseId) {
        if (!isAdmin(role)) {
            throw new IllegalArgumentException("Only Admins can delete courses.");
        }

        // Check if the course exists before deleting
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("Course with ID " + courseId + " not found.");
        }
        courseRepository.delete(courseId);
    }

    public void updateCourse(String id, Course updatedCourse, UserRole role) {
        if (!isInstructorOrAdmin(role)) {
            throw new IllegalArgumentException("Only Instructors or Admins can update courses.");
        }

        Optional<Course> existingCourse = courseRepository.findById(id);
        if (existingCourse.isEmpty()) {
            throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
        }

        if (courseRepository.existsByTitle(updatedCourse.getTitle())
                && !existingCourse.get().getTitle().equalsIgnoreCase(updatedCourse.getTitle())) {
            throw new IllegalArgumentException("A course with the title '" + updatedCourse.getTitle() + "' already exists.");
        }

        if (courseRepository.existsById(updatedCourse.getId()) && !id.equals(updatedCourse.getId())) {
            throw new IllegalArgumentException("Another course with the ID " + updatedCourse.getId() + " already exists.");
        }

        courseRepository.delete(id);
        courseRepository.save(updatedCourse);
    }

    private boolean isInstructorOrAdmin(UserRole role) {
        return role == UserRole.INSTRUCTOR || role == UserRole.ADMIN;
    }

    private boolean isAdmin(UserRole role) {
        return role == UserRole.ADMIN;
    }
}