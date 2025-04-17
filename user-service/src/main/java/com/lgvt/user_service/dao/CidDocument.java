package com.lgvt.user_service.dao;

import java.math.BigInteger;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CidDocument {
    private String document_cloudinary_id;
    private String document_cloudinary_url;
}
