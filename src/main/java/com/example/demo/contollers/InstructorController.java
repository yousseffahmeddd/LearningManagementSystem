package com.example.demo.contollers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instructor")
public class InstructorController {
    @RequestMapping
    public String sayHello() {
        return "Hello, Instructor!";
    }
}
