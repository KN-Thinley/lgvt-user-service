package com.lgvt.user_service.service;

import org.springframework.web.multipart.MultipartFile;

import com.lgvt.user_service.entity.Voter;

public interface VoterService {
    Voter saveVoter(Voter voter, MultipartFile imageFile);
}
