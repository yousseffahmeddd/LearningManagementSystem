package com.example.demo.service;

import com.example.demo.models.Assignment;
import com.example.demo.models.Quiz;
import com.example.demo.models.Submission;
import com.example.demo.models.UserRole;
import com.example.demo.repository.AssignmentRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AssignmentService {
    @Autowired
    private  AssignmentRepository assignmentRepository;
    private  SubmissionRepository submissionRepository;
    private  FileStorageService fileStorageService;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository,
                             SubmissionRepository submissionRepository,
                             FileStorageService fileStorageService) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.fileStorageService = fileStorageService;
    }



    private final AtomicInteger submissionIdCounter = new AtomicInteger(1);

    public Submission submitAssignmentWithSequentialId(String studentId, String assignmentId, MultipartFile file) {
        // Generate a new ID sequentially
        int newId = submissionIdCounter.getAndIncrement();

        // Handle file metadata
        String fileName = file.getOriginalFilename();
        String filePath = "uploads/" + fileName;

        // Create a new submission object with sequential ID

        return new Submission(
                String.valueOf(newId), // Converts the `int` to a `String` for the ID
                assignmentId,
                studentId,
                fileName,
                filePath
        );
    }
    public List<Submission> getAllSubmissions(UserRole role) {
        if (role != UserRole.INSTRUCTOR) {
            throw new SecurityException("Access denied: Only instructors are allowed to view submissions.");
        }
        return submissionRepository.findAllSubmissions();
    }

    public Assignment createAssignment(String id,String title, String description, String courseId, UserRole role) {
        if (role != UserRole.INSTRUCTOR && role != UserRole.ADMIN) {
            throw new IllegalArgumentException("Only instructors or admins can create assignments.");
        }
        Assignment assignment = new Assignment();
        assignment.setId(id);
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setCourseId(courseId);

        return assignmentRepository.save(assignment);
    }

    public Submission submitAssignment(String studentId, String assignmentId, MultipartFile file) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new IllegalArgumentException("Assignment with ID " + assignmentId + " does not exist.");
        }
        int newId = submissionIdCounter.getAndIncrement();
        // Save file
        String fileUrl = fileStorageService.uploadFile(file);

        Submission submission = new Submission();
        submission.setId(String.valueOf(newId));
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(studentId);
        submission.setFileName(file.getOriginalFilename());
        submission.setFileUrl(fileUrl);

        return submissionRepository.save(submission);
    }

    public List<Assignment> getAllassignments() {
        return assignmentRepository.findAll();
    }
}