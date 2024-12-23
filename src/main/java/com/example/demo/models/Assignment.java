package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    private String id;
    private String title;
    private String description;
    private String courseId;
    private String feedback;
    private String grade;


}