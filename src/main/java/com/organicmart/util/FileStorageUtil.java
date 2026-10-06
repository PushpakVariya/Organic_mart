package com.organicmart.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private final Path uploadDir = Paths.get("uploads");

    public String store(MultipartFile file, String subFolder) {
        try {
            Path targetDir = uploadDir.resolve(subFolder);
            Files.createDirectories(targetDir);

            String extension = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID() + extension;
            Path targetPath = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath);

            return "/uploads/" + subFolder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }
}
