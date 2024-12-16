package com.example.demo.repository;

import com.example.demo.models.Course;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class CourseRepository {
    private final Map<String, Course> courses = new HashMap<>();

    public Course save(String id, String title, String description , Integer hours) {
        Course course = new Course(id, title, description , hours);
        courses.put(course.getId(), course);
        return course;
    }

    public Optional<Course> findById(String id) {
        return Optional.ofNullable(courses.get(id));
    }

    public void delete(String id) {
        courses.remove(id);
    }

    public Collection<Course> findAll() {
        return courses.values();
    }

    public boolean existsById(String id) {
        return courses.containsKey(id);
    }

    // Check if a course exists by title
    public boolean existsByTitle(String title) {
        return courses.values().stream()
                .anyMatch(course -> course.getTitle().equalsIgnoreCase(title));
    }


}
