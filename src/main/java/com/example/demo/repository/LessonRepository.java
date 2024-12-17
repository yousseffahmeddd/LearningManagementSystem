package com.example.demo.repository;

import com.example.demo.models.Lesson;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class LessonRepository {

    private final Map<Long, Lesson> lessons = new HashMap<>();
    private Long nextId = 0L;

    public void save(String courseId, String title) {
        Lesson lesson = new Lesson(++nextId, courseId, title);
        lessons.put(lesson.getId(), lesson);
    }

    public Collection<Lesson> findAll() {
        return lessons.values();
    }

    /*public boolean existsById(Long id) {
        return lessons.containsKey(id);
    }*/

    // Check if a course exists by title
    public boolean existsByTitle(String title) {
        return lessons.values().stream()
                .anyMatch(course -> course.getTitle().equalsIgnoreCase(title));
    }

    public boolean existsByTitleAndCourseId(String title, String courseId) {
        return lessons.values().stream()
                .anyMatch(lesson -> lesson.getTitle().equalsIgnoreCase(title) &&
                        lesson.getCourseId().equals(courseId));
    }
}
