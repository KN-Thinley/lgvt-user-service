package com.lgvt.user_service.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.lgvt.user_service.security.CustomDetailsService;
import com.lgvt.user_service.service.JwtService;
import com.lgvt.user_service.service.SecureTokenService;
import com.lgvt.user_service.service.VoterService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class VoterRestController {
    private VoterService voterService;
    private SecureTokenService secureTokenService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomDetailsService customUserDetailsService;

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
    public ResponseEntity<Map<String, String>> verifyLoginOTP(@RequestParam("otp") int otp,
            @RequestParam("token") String token,
            HttpServletResponse response) {
        try {
            boolean isVerified = secureTokenService.verifyOtp(otp, token);
            if (isVerified) {
                secureTokenService.changeVoterLoginStatus(token);

                // Create Session
                String email = secureTokenService.getEmailFromToken(token); // Extract email from token
                String sessionToken = voterService.createSession(email, response);

                // Remove Token
                secureTokenService.removeToken(sessionToken);

                // Prepare response object
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("message", "OTP verified successfully");
                responseBody.put("session_token", sessionToken);

                // Return response
                return ResponseEntity.ok(responseBody);
            } else {
                // Prepare error response
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid OTP");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (RuntimeException e) {
            // Prepare error response
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/voter/logout")
    public ResponseEntity<String> logout(Authentication authentication, HttpServletResponse response) {
        String email = authentication.getName();
        return voterService.logout(email, response);
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
    public ResponseEntity<Map<String, Object>> getVoterInfo(Authentication authentication) {
        String email = authentication.getName();
        return voterService.getVoterInfoByEmail(email);
    }

    @PostMapping("/voter/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String password, Authentication authentication) {
        String email = authentication.getName();
        return voterService.updatePassword(password, email);
    }
}
