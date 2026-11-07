package com.jerzymaj.uploadservice.service;

import com.jerzymaj.uploadservice.dto.UploadResult;
import com.jerzymaj.uploadservice.exceptions.FileEmptyException;
import com.jerzymaj.uploadservice.exceptions.FiletypeNotAllowedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UploadService {

    @Value("${storage.upload-dir:upload-dir}")
    public Path UPLOAD_DIR;

    @Value("${app.allowed-extensions}")
    public List<String> allowedExtensions;

    public UploadResult uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileEmptyException("File is empty");
        }

        if (!isExtensionAllowed(file)) {
            throw new FiletypeNotAllowedException("File type not allowed");
        }

        String folderId = UUID.randomUUID().toString();

        copyFileToUploadDir(file, folderId);

        return new UploadResult(folderId, "File uploaded successfully");
    }

    private boolean isExtensionAllowed(MultipartFile file) {
        String name = file.getOriginalFilename();
        int dotIndex = name.lastIndexOf(".");

        if (dotIndex == -1) {
            return false;
        }

        String extension = name.substring(dotIndex).toLowerCase(Locale.ROOT);

        if (!allowedExtensions.contains(extension)) {
            return false;
        }

        return true;
    }

    private void copyFileToUploadDir(MultipartFile file, String folderId) {
        try {
            Path uploadDir = UPLOAD_DIR.resolve(folderId).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(file.getOriginalFilename()).normalize();

            if (!filePath.startsWith(uploadDir)) {
                throw new SecurityException("Invalid file path: " + file.getOriginalFilename());
            }

            Files.copy(file.getInputStream(), filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create upload directory", ex);
        }
    }
}
