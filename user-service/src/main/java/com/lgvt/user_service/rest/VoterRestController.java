package com.lgvt.user_service.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgvt.user_service.Response.ForgotPasswordResponse;
import com.lgvt.user_service.Response.VerifyForgotPasswordResponse;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.service.SecureTokenService;
import com.lgvt.user_service.service.VoterService;

import jakarta.servlet.http.HttpServletResponse;
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

    // If user leaves half way
    // If user token gets expired
    @PostMapping("/voter/register")
    public ResponseEntity<?> save(@Valid @RequestPart Voter voter, @RequestPart MultipartFile file) {
        // Save the voter
        String token = voterService.saveVoter(voter, file);

        // Response Body Preparation
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Voter with CID " + voter.getCid() + " has been successfully created.");
        response.put("token", token);

        // Send the OTP to the email
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam("otp") int otp, @RequestParam("token") String token) {
        try {
            boolean isVerified = secureTokenService.verifyOtp(otp, token);
            if (isVerified) {
                secureTokenService.changeVoterStatus(token);
                secureTokenService.removeToken(token);
                return ResponseEntity.ok("OTP is verified");
            } else {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/voter/login")
    public ResponseEntity<?> login(@RequestBody Voter voter, HttpServletResponse response) {
        return voterService.loginVoter(voter, response);
    }

    @PostMapping("/voter/verify-login-otp")
    public ResponseEntity<String> verifyLoginOTP(@RequestParam("otp") int otp, @RequestParam("token") String token) {
        try {
            boolean isVerified = secureTokenService.verifyOtp(otp, token);
            if (isVerified) {
                secureTokenService.changeVoterLoginStatus(token);
                secureTokenService.removeToken(token);
                return ResponseEntity.ok("OTP is verified");
            } else {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/voter/logout")
    public ResponseEntity<String> logout(@RequestBody Voter voter, HttpServletResponse response) {
        return voterService.logout(voter, response);
    }

    @PostMapping("/voter/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody Voter voter) {
        return voterService.forgotPassword(voter);
    }

    @PostMapping("/voter/verify-forgot-password-otp")
    public ResponseEntity<VerifyForgotPasswordResponse> verifyForgotPasswordOTP(@RequestParam("otp") int otp,
            @RequestParam("token") String token) {

        try {
            boolean isVerified = secureTokenService.verifyOtp(otp, token);
            if (isVerified) {
                // Return success response with the token
                return ResponseEntity.ok(new VerifyForgotPasswordResponse("OTP is verified", token));
            } else {
                // Return error response with null token
                return ResponseEntity.badRequest().body(new VerifyForgotPasswordResponse("Invalid OTP", null));
            }
        } catch (RuntimeException e) {
            // Return error response with null token
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new VerifyForgotPasswordResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/voter/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request,
            @RequestParam("token") String token) {
        // Extract the password from the request body
        String password = request.get("password");

        // Call the service to reset the password
        return voterService.resetPassword(password, token);
    }

    @PostMapping("/voter/resent-otp")
    public ResponseEntity<String> resentOTP(@RequestParam("token") String token, @RequestParam("type") String type) {
        return voterService.resentOTP(token, type);
    }

    @GetMapping("/voter/info")
    public boolean getVoterInfo() {
        return true;
    }
}
