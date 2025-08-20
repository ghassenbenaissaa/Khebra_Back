package com.example.Khebra.service.impl;


import com.example.Khebra.entity.Image;
import com.example.Khebra.entity.User;
import com.example.Khebra.repository.ImageRepository;
import com.example.Khebra.security.JwtService;
import com.example.Khebra.service.IImageService;
import com.example.Khebra.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

    private final ImageRepository repo;
    private final JwtService jwtService;
    private final IUserService userService;

    @Override
    public void SaveImage(String authHeader, Image newImage) {
        String token = jwtService.extractToken(authHeader);
        String email = jwtService.extractUsername(token);
        User user = userService.getUserEntityByEmail(email);

        // Find existing image for user
        Optional<Image> existingImageOpt = repo.findByUserId(user.getId());

        if (existingImageOpt.isPresent()) {
            Image existingImage = existingImageOpt.get();
            existingImage.setName(newImage.getName());
            existingImage.setImageUrl(newImage.getImageUrl());
            existingImage.setImageId(newImage.getImageId());
            repo.save(existingImage);
        } else {
            // No image yet, set user and save new image
            newImage.setUser(user);
            repo.save(newImage);
        }
    }

}
