package com.lgvt.user_service.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.lgvt.user_service.Response.LoginResponse;
import com.lgvt.user_service.entity.Voter;

public interface VoterService {
    String saveVoter(Voter voter, MultipartFile imageFile);

    LoginResponse loginVoter(Voter voter);

    ResponseEntity<String> logout(Voter voter);

    ResponseEntity<String> forgotPassword(Voter voter);

    ResponseEntity<String> resetPassword(Voter voter);
}
