package com.jerzymaj.uploadservice.unit;

import com.jerzymaj.uploadservice.dto.UploadResult;
import com.jerzymaj.uploadservice.exceptions.FileEmptyException;
import com.jerzymaj.uploadservice.service.UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class UploadServiceUnitTests {

    @TempDir
    Path tempDir;

    private UploadService uploadService;

    @BeforeEach
    void setUp() {
        uploadService = new UploadService();
        uploadService.UPLOAD_DIR = tempDir;
        uploadService.allowedExtensions = List.of(".mp3", ".wav");
    }

    @Test
    public void shouldThrow_WhenFileIsEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", new byte[0]);

        assertThrows(FileEmptyException.class, () -> uploadService.uploadFile(file));
    }

    @Test
    public void shouldSaveFile_WhenFileIsValid() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", "audio".getBytes());

        UploadResult result = uploadService.uploadFile(file);

        assertTrue(Files.exists(tempDir.resolve(result.folderId()).resolve("test.mp3")));
    }
}