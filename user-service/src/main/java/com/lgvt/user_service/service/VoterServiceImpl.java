package com.lgvt.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lgvt.user_service.dao.CidDocument;
import com.lgvt.user_service.dao.VoterDAO;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.exception.UserAlreadyExistException;
import com.lgvt.user_service.utils.FileUploadUtil;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.Data;

@Service
@Data
public class VoterServiceImpl implements VoterService {
    @Autowired
    private VoterDAO voterDAO;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public Voter saveVoter(@Valid Voter voter, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            final CidDocument cidDocument = uploadImage(voter.getId(), imageFile);
            voter.setCid_document(cidDocument);
        }

        if (voterDAO.checkIfUserExists(voter.getEmail())) {
            throw new UserAlreadyExistException("This Voter already exists");
        } else {
            Voter voter_res = voterDAO.saveVoter(voter);
            voterDAO.sendRegistrationConfirmationEmail(voter_res);
            return voter_res;
        }
    }

    public CidDocument uploadImage(final Integer id, final MultipartFile imageFile) {
        FileUploadUtil.assertAllowed(imageFile, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(imageFile.getOriginalFilename());
        final CidDocument response = cloudinaryService.uploadFile(imageFile, fileName);
        return response;
    }

}
