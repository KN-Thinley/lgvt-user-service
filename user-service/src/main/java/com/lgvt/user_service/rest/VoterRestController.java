package com.lgvt.user_service.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.service.SecureTokenService;
import com.lgvt.user_service.service.VoterService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class VoterRestController {
    private VoterService voterService;
    private SecureTokenService secureTokenService;

    @Autowired
    public VoterRestController(VoterService voterService, SecureTokenService secureTokenService) {
        this.secureTokenService = secureTokenService;
        this.voterService = voterService;
    }

    @PostMapping("/voter/register")
    public ResponseEntity<?> save(@Valid @RequestPart Voter voter, @RequestPart MultipartFile file) {
        // /register
        // Return proper response
        // Proper Error handling
        Voter voter_Res = voterService.saveVoter(voter, file);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Voter with CID " + voter_Res.getCid() + " has been successfully created.");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam("otp") int otp, @RequestParam("token") String token) {
        try {
            boolean isVerified = secureTokenService.verifyOtp(otp, token);
            if (isVerified) {
                secureTokenService.changeVoterStatus(token);
                return ResponseEntity.ok("OTP is verified");
            } else {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Need to return the tokens for mfa
    @PostMapping("/voter/login")
    public ResponseEntity<?> login(@RequestBody Voter voter) {
        return new ResponseEntity<>(voterService.loginVoter(voter), HttpStatus.OK);
    }

    // Login token verification > Might need it since using the verify-otp api works
    // but it updates both user verification and is_logged_in => For now leave it
    // Also need a separate api for verification since i need to create the session
    @PostMapping("/voter/verify-login-otp")
    public ResponseEntity<String> verifyLoginOTP(@RequestParam("otp") int otp, @RequestParam("token") String token) {
        // try {
        // boolean isVerified = secureTokenService.verifyOtp(otp, token);
        // if (isVerified) {
        // return ResponseEntity.ok("Login OTP is verified");
        // } else {
        // return ResponseEntity.badRequest().body("Invalid Login OTP");
        // }
        // } catch (RuntimeException e) {
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        // }

        return null;
    }

    @PostMapping("/voter/logout")
    public ResponseEntity<String> logout(@RequestBody Voter voter) {
        // Need to check if the user is logged in
        // If user is logged in then logout the user
        // If user is not logged in then return the session

        return voterService.logout(voter);
    }

    @PostMapping("/voter/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Voter voter) {
        // Receive a email
        // Make sure the email is valid
        // Send the otp to the email
        // Return the response with token
        return voterService.forgotPassword(voter);
    }

    @PostMapping("/voter/verify-forgot-password-otp")
    public ResponseEntity<String> verifyForgotPasswordOTP(@RequestParam("otp") int otp,
            @RequestParam("token") String token) {
        // Verify the otp
        // If otp is valid then return the response with token
        // If otp is not valid then return the response with error message
        return null;
    }

    // I need to change the RequestBody type since it is no longer Voter
    @PostMapping("/voter/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Voter voter) {
        // Receive the token
        // Verify the token
        // If token is valid then reset the password
        // Return the response with token
        return voterService.resetPassword(voter);
    }
}
