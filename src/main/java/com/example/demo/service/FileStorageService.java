package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    private static final String UPLOAD_DIR = "uploads/";

    public String uploadFile(MultipartFile file) {
        try {
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String fileName = file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }
}