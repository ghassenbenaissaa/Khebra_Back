package com.example.Khebra.service;

import com.example.Khebra.entity.Image;

public interface IImageService {

    void SaveImage(String authHeader, Image image);
}
