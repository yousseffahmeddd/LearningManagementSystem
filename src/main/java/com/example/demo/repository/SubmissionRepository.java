package com.example.demo.repository;

import com.example.demo.models.Submission;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SubmissionRepository {
    private final List<Submission> submissions = new ArrayList<>();

    public Submission save(Submission submission) {
        submissions.add(submission);
        return submission;
    }

    public List<Submission> findByAssignmentId(String assignmentId) {
        return submissions.stream()
                .filter(submission -> submission.getAssignmentId().equals(assignmentId))
                .collect(Collectors.toList());
    }
    public List<Submission> findAllSubmissions() {
        return new ArrayList<>(submissions);
    }

}