package com.example.Khebra.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component

public class CloudinaryConfig {

    private final CloudinaryConfigProperties configProperties;

    public CloudinaryConfig(CloudinaryConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", configProperties.getCloudName(),
                "api_key", configProperties.getApiKey(),
                "api_secret", configProperties.getApiSecret()
        ));
    }
}
