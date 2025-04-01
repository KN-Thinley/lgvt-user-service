package com.lgvt.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        final Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "lgvt");
        config.put("api_key", "123456789");
        config.put("api_secret", "123456789");

        return new Cloudinary(config);
    }
}
