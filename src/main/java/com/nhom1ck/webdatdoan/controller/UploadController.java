package com.nhom1ck.webdatdoan.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nhom1ck.webdatdoan.dto.common.ApiResponse;
import com.nhom1ck.webdatdoan.exception.BadRequestException;

/**
 * Upload image endpoint for admin product/category images.
 */
@RestController
@RequestMapping("/api/upload")
@PreAuthorize("hasRole('ADMIN')")
public class UploadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String originalName = file.getOriginalFilename() != null
                    ? file.getOriginalFilename()
                    : "image";
            String cleanName = Paths.get(originalName).getFileName().toString();

            String extension = "";
            int dotIndex = cleanName.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < cleanName.length() - 1) {
                extension = cleanName.substring(dotIndex);
            }

            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            File targetFile = uploadPath.resolve(fileName).toFile();
            file.transferTo(targetFile);

            String relativePath = "/uploads/" + fileName;

            Map<String, String> data = new LinkedHashMap<>();
            data.put("filename", fileName);
            data.put("path", relativePath);
            data.put("url", relativePath);
            data.put("originalName", cleanName);

            return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", data));
        } catch (IOException ex) {
            throw new BadRequestException("Could not save uploaded image");
        }
    }
}
