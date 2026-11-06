package com.jerzymaj.uploadservice.controller;


import com.jerzymaj.uploadservice.dto.UploadResult;
import com.jerzymaj.uploadservice.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/too-long-to-listen/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    public final UploadService uploadService;

    @PostMapping
    public ResponseEntity<UploadResult> uploadFile(MultipartFile file) {

        UploadResult result = uploadService.uploadFile(file);

        return ResponseEntity.ok(result);
    }
}
