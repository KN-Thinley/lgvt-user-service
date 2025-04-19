package com.lgvt.user_service.service;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lgvt.user_service.Response.ForgotPasswordResponse;
import com.lgvt.user_service.Response.LoginResponse;
import com.lgvt.user_service.dao.CidDocument;
import com.lgvt.user_service.dao.VoterDAO;
import com.lgvt.user_service.entity.SecureToken;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.exception.UserAlreadyExistException;
import com.lgvt.user_service.security.CustomDetailsService;
import com.lgvt.user_service.utils.FileUploadUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    @Autowired
    private SecureTokenService secureTokenService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomDetailsService customUserDetailsService;

    @Override
    @Transactional
    public String saveVoter(@Valid Voter voter, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            final CidDocument cidDocument = uploadImage(voter.getId(), imageFile);
            voter.setCid_document(cidDocument);
        }

        if (voterDAO.checkIfUserExists(voter.getEmail())) {
            throw new UserAlreadyExistException("This Voter already exists");
        } else {
            Voter voter_res = voterDAO.saveVoter(voter);
            // Send Email
            String token = voterDAO.sendRegistrationConfirmationEmail(voter_res);
            return token;
        }
    }

    public CidDocument uploadImage(final Integer id, final MultipartFile imageFile) {
        FileUploadUtil.assertAllowed(imageFile, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(imageFile.getOriginalFilename());
        final CidDocument response = cloudinaryService.uploadFile(imageFile, fileName);
        return response;
    }

    public ResponseEntity<LoginResponse> loginVoter(Voter voter, HttpServletResponse response) {
        Voter existingVoter = voterDAO.getVoterByEmail(voter.getEmail());

        if (existingVoter != null) {
            // Check if the password is correct
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(voter.getEmail(),
                            voter.getPassword()));
            if (authentication.isAuthenticated()) {
                // Check if the user is verified
                if (existingVoter.isVerified()) {
                    // Check if the user has done MFA
                    if (existingVoter.isLogged_in()) {

                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(voter.getEmail());
                        String token = jwtService.generateToken(userDetails);

                        // Set JWT as a cookie
                        Cookie jwtCookie = new Cookie("JWT-TOKEN", token);
                        jwtCookie.setHttpOnly(true);
                        response.addCookie(jwtCookie);

                        return ResponseEntity.ok(new LoginResponse(
                                "Login successful",
                                token,
                                true,
                                "proceed"));
                    } else {
                        // Redirect to MFA page
                        String token = voterDAO.sendLoginMFAEmail(existingVoter);
                        // Generate and send a email
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(
                                "Multifactor Authentication needed",
                                token,
                                false,
                                "redirect_to_mfa"));
                    }
                } else {
                    // User is not verified
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new LoginResponse(
                            "User is not verified",
                            null,
                            false,
                            "verify_user"));
                }
            } else {
                // Password is incorrect
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(
                        "Incorrect password",
                        null,
                        false,
                        "retry_login"));
            }
        } else {
            // User does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginResponse(
                    "User does not exist",
                    null,
                    false,
                    "register_user"));
        }
    }

    public ResponseEntity<String> logout(Voter voter, HttpServletResponse response) {
        // Check if the user exists
        // Check if the user is logged in
        // If the user is logged in then change the status of the login to false

        Voter existingVoter = voterDAO.getVoterByEmail(voter.getEmail());

        if (existingVoter != null) {
            if (existingVoter.isLogged_in()) {
                // DB Changes
                voterDAO.logoutVoter(existingVoter);

                // ་Cookie Clearing
                Cookie jwtCookie = new Cookie("JWT-TOKEN", null);
                jwtCookie.setMaxAge(0);
                response.addCookie(jwtCookie);
                return ResponseEntity.ok().build();
            } else {
                // User is not logged in
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already logged out");
            }
        } else {
            // User is not logged in
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }
    }

    public ResponseEntity<ForgotPasswordResponse> forgotPassword(Voter voter) {
        // Check if the user exists
        Voter existingVoter = voterDAO.getVoterByEmail(voter.getEmail());
        if (existingVoter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ForgotPasswordResponse("User with the provided email does not exist.", null));
        }

        // Use voterDAO to send the OTP and email
        try {
            String token = voterDAO.sendForgotPasswordEmail(existingVoter);
            // Return a success response with the token
            return ResponseEntity.ok(new ForgotPasswordResponse(
                    "OTP has been sent to your email. Use the token for further verification.", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ForgotPasswordResponse("Failed to send OTP to email.", null));
        }
    }

    public ResponseEntity<String> resetPassword(String password, String token) {
        // Retrieve the SecureToken using the token
        SecureToken secureToken = secureTokenService.findByToken(token);

        if (secureToken == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid token. Please request a new one.");
        }

        // Check if the token is expired
        if (secureToken.getExpireAt().isBefore(LocalDateTime.now())) {
            secureTokenService.removeToken(token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired. Please request a new one.");
        }

        // Retrieve the associated Voter using the token
        Voter existingVoter = secureToken.getVoter();
        System.out.println("Existing Voter: " + existingVoter);

        if (existingVoter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No voter associated with this token.");
        }

        // Pass the voter's old password to the passwordReset method
        voterDAO.passwordReset(password, existingVoter);

        // Remove the token after successful password reset
        secureTokenService.removeToken(token);

        // Return a success response
        return ResponseEntity.ok("Password has been successfully reset.");
    }

    public ResponseEntity<String> resentOTP(String token, String type) {
        // Fetch the SecureToken using the token
        SecureToken secureToken = secureTokenService.findByToken(token);

        if (secureToken == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid token. Please request a new one.");
        }

        try {
            String newToken = voterDAO.resentOTP(secureToken, type);

            // Return the new token in the response
            return ResponseEntity.ok("A new OTP has been sent to your email. Token: " + newToken);
        } catch (Exception e) {
            // Handle any exceptions during the process
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to resend OTP.");
        }
    }
}
