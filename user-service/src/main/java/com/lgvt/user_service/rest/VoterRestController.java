package com.lgvt.user_service.rest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgvt.user_service.Response.ForgotPasswordResponse;
import com.lgvt.user_service.Response.LoginResponse;
import com.lgvt.user_service.Response.LoginUserInfo;
import com.lgvt.user_service.Response.VerifyForgotPasswordResponse;
import com.lgvt.user_service.dao.SecureTokenDAO;
import com.lgvt.user_service.dto.AuditDto;
import com.lgvt.user_service.entity.Gender;
import com.lgvt.user_service.entity.SecureToken;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.feign.AuditFeign;
import com.lgvt.user_service.security.CustomDetailsService;
import com.lgvt.user_service.service.JwtService;
import com.lgvt.user_service.service.SecureTokenService;
import com.lgvt.user_service.service.UserService;
import com.lgvt.user_service.service.VoterService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/auth")
public class VoterRestController {
    private VoterService voterService;
    private UserService userService;
    private SecureTokenService secureTokenService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomDetailsService customUserDetailsService;

    @Autowired
    private SecureTokenDAO secureTokenDAO;

    @Autowired
    private AuditFeign auditFeign;

    @Autowired
    public VoterRestController(VoterService voterService, SecureTokenService secureTokenService,
            UserService userService) {
        this.secureTokenService = secureTokenService;
        this.voterService = voterService;
        this.userService = userService;
    }

    // If user leaves half way
    // If user token gets expired
    @PostMapping(value = "/v2/voter/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> save(@RequestPart Voter voter, @RequestPart MultipartFile file) {
        // Save the voter

        System.out.println("Voter: " + voter);
        System.out.println("File: " + file.getOriginalFilename());
        String token = voterService.saveVoter(voter, file);

        // Response Body Preparation
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Voter with CID " + voter.getCid() + " has been successfully created.");
        response.put("token", token);

        // Send the OTP to the email
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/voter/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveVoter(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("cid") String cid,
            @RequestParam("phone") String phone,
            @RequestParam("dzongkhag") String dzongkhag,
            @RequestParam("gewog") String gewog,
            @RequestParam("village") String village,
            @RequestParam("dob") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dob,
            @RequestParam("gender") Gender gender,
            @RequestParam("occupation") String occupation,
            @RequestPart("file") MultipartFile file) {
        Voter voter = new Voter();
        voter.setName(name);
        voter.setEmail(email);
        voter.setPassword(password);
        voter.setCid(cid);
        voter.setPhone(phone);
        voter.setDzongkhag(dzongkhag);
        voter.setGewog(gewog);
        voter.setVillage(village);
        voter.setDob(dob);
        voter.setGender(gender);
        voter.setOccupation(occupation);

        String token = voterService.saveVoter(voter, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Voter with CID " + voter.getCid() + " has been successfully created.");
        response.put("token", token);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> checkIfUserExists(@PathVariable int id) {
        boolean exists = voterService.checkIfUserExistsById(id);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/voter/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOTP(@RequestParam("otp") int otp,
            @RequestParam("token") String token) {
        try {
            boolean isVerified = secureTokenService.verifyOtp(otp, token);
            if (isVerified) {
                secureTokenService.changeVoterStatus(token);
                secureTokenService.removeToken(token);
                return ResponseEntity.ok(Map.of("message", "OTP is verified"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid OTP"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/voter/login")
    public ResponseEntity<?> login(@RequestBody Voter voter, HttpServletResponse response) {
        return voterService.loginVoter(voter, response);
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<LoginResponse> verifyLoginOTP(@RequestParam("otp") int otp,
            @RequestParam("token") String token,
            HttpServletResponse response) {
        try {
            boolean isVerified = secureTokenService.verifyOtp(otp, token);
            if (isVerified) {
                SecureToken secureToken = secureTokenDAO.findByToken(token);

                if (secureToken.getVoter() != null) {
                    secureTokenService.changeVoterLoginStatus(token);
                }

                // Create Session
                String email = secureTokenService.getEmailFromToken(token); // Extract email from token
                String sessionToken = voterService.createSession(email, response);

                // Remove Token
                secureTokenService.removeToken(sessionToken);

                // // Prepare response object
                // Map<String, Object> responseBody = new HashMap<>();
                // responseBody.put("message", "OTP verified successfully");
                // responseBody.put("token", sessionToken);

                // Add user details in a nested object
                LoginUserInfo userInfo;
                if (secureToken.getVoter() != null) {
                    userInfo = new LoginUserInfo(
                            secureToken.getVoter().getId(),
                            secureToken.getVoter().getEmail(),
                            secureToken.getVoter().getName(),
                            secureToken.getVoter().getRole().toString());
                } else {
                    userInfo = new LoginUserInfo(
                            secureToken.getUser().getId(),
                            secureToken.getUser().getEmail(),
                            secureToken.getUser().getName(),
                            secureToken.getUser().getRole().toString());
                }
                // responseBody.put("user", user);
                if (secureToken.getVoter() != null) {
                    // AuditDto audit = new AuditDto(
                    //         email,
                    //         "AUTH_SUCCESS",
                    //         "Voter Logged In successfully",
                    //         null,
                    //         "SUCCESS");
                    // auditFeign.createAudit(audit);
                } else {
                    // AuditDto audit = new AuditDto(
                    //         email,
                    //         "AUTH_SUCCESS",
                    //         "User Logged In successfully",
                    //         null,
                    //         "SUCCESS");
                    // auditFeign.createAudit(audit);
                }
                // Return success response
                return ResponseEntity.ok(new LoginResponse(
                        "OTP verified successfully",
                        sessionToken,
                        true,
                        "proceed",
                        userInfo));
            } else {
                // Return error response for invalid OTP
                return ResponseEntity.badRequest().body(new LoginResponse(
                        "Invalid OTP",
                        null,
                        false,
                        "retry",
                        null));
            }
        } catch (RuntimeException e) {
            // Return error response for exceptions
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse(
                    e.getMessage(),
                    null,
                    false,
                    "error",
                    null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication, HttpServletResponse response) {
        String email = authentication.getName();

        // Check the roles or authorities of the authenticated user
        boolean isVoter = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("VOTER"));
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN")
                        || authority.getAuthority().equals("SUPER_ADMIN"));

        if (isVoter) {
            // Handle logout for Voter
            return voterService.logout(email, response);
        } else if (isUser) {
            // Handle logout for User
            return userService.logout(email, response);
        } else {
            // If no matching role is found, return an error response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized user type");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email"); // Extract the email from the request body
        return voterService.forgotPassword(email);
    }

    @PostMapping("/verify-forgot-password-otp")
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

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request,
            @RequestParam("token") String token) {
        // Extract the password from the request body
        String password = request.get("password");

        // Call the service to reset the password
        return voterService.resetPassword(password, token);
    }

    @PostMapping("/resent-otp")
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

    @PutMapping("/voter/update-info")
    public ResponseEntity<Map<String, String>> updateVoterInfo(
            @RequestBody Map<String, String> updates,
            Authentication authentication) {

        String email = authentication.getName();

        try {
            // Call the service to update voter information
            voterService.updateVoterInfoByEmail(email, updates);
            return ResponseEntity.ok(Map.of("message", "Voter information updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred while updating voter information"));
        }
    }

}