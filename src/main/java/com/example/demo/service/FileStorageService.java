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
        String fileName = file.getOriginalFilename();

        // Ensure file name is not null
        if (fileName == null) {
            throw new IllegalArgumentException("Invalid file name");
        }

        // Determine target file path
        Path targetLocation = Paths.get("uploads").resolve(fileName);

        try {
            // Check if the file already exists
            if (Files.exists(targetLocation)) {
                // Append a unique identifier to avoid overwriting
                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                targetLocation = Paths.get("uploads").resolve(uniqueFileName);
            }

            // Save the file to the target location
            Files.copy(file.getInputStream(), targetLocation);

            // Return the stored file's URI or file name
            return targetLocation.toString();

        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file " + fileName, ex);
        }
    }
}