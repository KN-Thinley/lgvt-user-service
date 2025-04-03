package com.lgvt.user_service.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.lgvt.user_service.dao.CidDocument;
import com.lgvt.user_service.utils.FileUploadUtil;

import jakarta.transaction.Transactional;
import lombok.Data;

@Service
@Data
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    @Transactional
    public CidDocument uploadFile(final MultipartFile file, final String fileName) {
        try {
            final Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    Map.of("public_id", "lgvt/cid/" + fileName));
            final String url = (String) uploadResult.get("url");
            final String publicId = (String) uploadResult.get("public_id");

            return CidDocument.builder()
                    .document_cloudinary_url(url)
                    .document_cloudinary_id(publicId)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image");
        }
    }
}
