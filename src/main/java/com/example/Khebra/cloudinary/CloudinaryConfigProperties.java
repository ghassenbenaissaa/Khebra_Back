package com.example.Khebra.cloudinary;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@Component
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryConfigProperties {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
}
