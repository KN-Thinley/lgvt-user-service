package com.lgvt.user_service.dao;

import java.math.BigInteger;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Embeddable
@Data
@Builder
public class CidDocument {
    private String document_cloudinary_id;
    private String document_cloudinary_url;
}
