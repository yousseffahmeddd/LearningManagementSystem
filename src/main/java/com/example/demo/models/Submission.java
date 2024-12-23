package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    private String id; // Unique submission ID
    private String assignmentId; // Related assignment
    private String studentId; // Student who submitted
    private String fileName; // Name of the uploaded file
    private String fileUrl; // Path or URL of the file
}