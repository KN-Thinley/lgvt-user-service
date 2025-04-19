package com.lgvt.user_service.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.lgvt.user_service.Response.ForgotPasswordResponse;
import com.lgvt.user_service.Response.LoginResponse;
import com.lgvt.user_service.entity.Voter;

import jakarta.servlet.http.HttpServletResponse;

public interface VoterService {
    String saveVoter(Voter voter, MultipartFile imageFile);

    ResponseEntity<LoginResponse> loginVoter(Voter voter, HttpServletResponse response);

    ResponseEntity<String> logout(Voter voter, HttpServletResponse response);

    ResponseEntity<ForgotPasswordResponse> forgotPassword(Voter voter);

    ResponseEntity<String> resetPassword(String password, String token);

    ResponseEntity<String> resentOTP(String token, String type);
}
