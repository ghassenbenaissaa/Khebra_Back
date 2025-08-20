package com.example.Khebra.controller;


import com.example.Khebra.entity.Image;
import com.example.Khebra.entity.User;
import com.example.Khebra.repository.ImageRepository;
import com.example.Khebra.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/image")
@RequiredArgsConstructor

public class CloudinaryController {


    private final CloudinaryService cloudinaryService;
    private final ImageRepository imageRepository;

    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String Token) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No file provided"));
        }

        try {
            String imageUrl = cloudinaryService.uploadImage(Token,file);
            return ResponseEntity.ok(Map.of(
                    "message", "Image uploaded successfully",
                    "url", imageUrl

            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-and-get")
    public ResponseEntity<?> uploadImageAndReturn(@RequestParam("file") MultipartFile file) {
        try {
            Image image = cloudinaryService.uploadAndSaveImage(file);
            return ResponseEntity.ok(image);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du téléchargement de l'image.");
        }
    }

}
