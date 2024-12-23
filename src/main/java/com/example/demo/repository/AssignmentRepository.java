package com.example.demo.repository;

import com.example.demo.models.Assignment;
import com.example.demo.models.Quiz;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AssignmentRepository {
    private  Map<String, Assignment> assignments = new HashMap<>();

    public Assignment save(Assignment assignment) {
        assignments.put(assignment.getId(), assignment);
        return assignment;
    }
    public List<Assignment> findAll() {
        return new ArrayList<>(assignments.values());
    }
    public Optional<Assignment> findById(String id) {
        return Optional.ofNullable(assignments.get(id));
    }

    public boolean existsById(String id) {
        return assignments.containsKey(id);
    }
}