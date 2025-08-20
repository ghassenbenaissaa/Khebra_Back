package com.example.Khebra.repository;

import com.example.Khebra.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    Optional<Image> findByUserId(Integer id);

    Optional<Image> findByimageId(String imageId);
}
