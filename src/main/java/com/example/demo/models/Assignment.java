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
    private String id; // Unique assignment ID
    private String title; // Assignment title
    private String description; // Assignment description
    private String courseId; // The course to which this assignment belongs


}