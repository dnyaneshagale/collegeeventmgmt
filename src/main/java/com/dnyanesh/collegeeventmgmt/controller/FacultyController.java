package com.dnyanesh.collegeeventmgmt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {
    @GetMapping("/hello")
    public String facultyHello() {
        return "Hello, Faculty!";
    }
}