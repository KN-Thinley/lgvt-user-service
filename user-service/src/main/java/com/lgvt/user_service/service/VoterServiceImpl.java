package com.lgvt.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lgvt.user_service.dao.CidDocument;
import com.lgvt.user_service.dao.VoterDAO;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.utils.FileUploadUtil;

import jakarta.transaction.Transactional;
import lombok.Data;

@Service
@Data
public class VoterServiceImpl implements VoterService {
    private VoterDAO voterDAO;
    private CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public Voter saveVoter(Voter voter, MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                final CidDocument cidDocument = uploadImage(voter.getId(), imageFile);
                voter.setCid_document(cidDocument);
            }
            return voterDAO.saveVoter(voter);
        } catch (Exception e) {
            throw new RuntimeException("Error saving voter: " + e.getMessage(), e);
        }
    }

    public CidDocument uploadImage(final Integer id, final MultipartFile imageFile) {
        FileUploadUtil.assertAllowed(imageFile, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(imageFile.getOriginalFilename());
        final CidDocument response = cloudinaryService.uploadFile(imageFile, fileName);
        return response;
    }

}
