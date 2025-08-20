package com.example.Khebra.service;

import com.cloudinary.Cloudinary;

import com.example.Khebra.entity.Image;
import com.example.Khebra.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final IImageService imageService;
    private final ImageRepository imageRepository;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif");



    public String uploadImage(String Token, MultipartFile file) {
        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("File type ." + extension + " is not supported");
        }

        try {
            Map<String, Object> uploadParams = Map.of(
                    "resource_type", "image",
                    "folder", "uploads"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            Image image = new Image(
                    filename,
                    uploadResult.get("secure_url").toString(),
                    uploadResult.get("public_id").toString()
            );
            imageService.SaveImage(Token, image);

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }

    public String uploadDomaineImage(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("File type ." + extension + " is not supported");
        }

        try {
            Map<String, Object> uploadParams = Map.of(
                    "resource_type", "image",
                    "folder", "uploads"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    public String extractPublicIdFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.split("\\.")[0];
    }

    public Image uploadAndSaveImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        String imageUrl = uploadDomaineImage(file);
        String imageId = extractPublicIdFromUrl(imageUrl);

        Optional<Image> existingImage = imageRepository.findByimageId(imageId);
        if (existingImage.isPresent()) {
            return existingImage.get();
        }

        Image image = new Image(file.getOriginalFilename(), imageUrl, imageId);
        return imageRepository.save(image);
    }

}
