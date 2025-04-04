package com.lgvt.user_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lgvt.user_service.Response.LoginResponse;
import com.lgvt.user_service.dao.CidDocument;
import com.lgvt.user_service.dao.VoterDAO;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.exception.UserAlreadyExistException;
import com.lgvt.user_service.utils.FileUploadUtil;
import com.lgvt.user_service.utils.SessionTokenGeneration;

import ch.qos.logback.core.subst.Token;
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

    // If user is logged in then return the session
    // If user is not logged in then redirect to the MFA page
    public LoginResponse loginVoter(Voter voter) {
        // Check if the user exists
        Voter existingVoter = voterDAO.getVoterByEmail(voter.getEmail());

        if (existingVoter != null) {
            // Check if the password is correct
            if (voterDAO.checkIfPasswordMatches(voter.getPassword(), existingVoter.getPassword())) {
                // Check if the user is verified
                if (existingVoter.isVerified()) {
                    // Check if the user is logged in
                    if (existingVoter.isLogged_in()) {
                        SessionTokenGeneration tokenGeneration = new SessionTokenGeneration();
                        String token = tokenGeneration.generateToken(existingVoter.getEmail());
                        return new LoginResponse(
                                "Login successful",
                                token,
                                true,
                                "proceed");
                    } else {
                        // Redirect to MFA page
                        String token = voterDAO.sendLoginMFAEmail(existingVoter);
                        // Generate and send a email
                        return new LoginResponse(
                                "Multifactor Authentication needed",
                                token,
                                false,
                                "redirect_to_mfa");
                    }
                } else {
                    // User is not verified
                    return new LoginResponse(
                            "User is not verified",
                            null,
                            false,
                            "verify_user");
                }
            } else {
                // Password is incorrect
                return new LoginResponse(
                        "Incorrect password",
                        null,
                        false,
                        "retry_login");
            }
        } else {
            // User does not exist
            return new LoginResponse(
                    "User does not exist",
                    null,
                    false,
                    "register_user");
        }
    }

    public ResponseEntity<String> logout(Voter voter) {
        // Check if the user exists
        // Check if the user is logged in
        // If the user is logged in then change the status of the login to false

        Voter existingVoter = voterDAO.getVoterByEmail(voter.getEmail());

        if (existingVoter != null) {
            if (existingVoter.isLogged_in()) {
                voterDAO.logoutVoter(existingVoter);
                return ResponseEntity.ok("Logout successful");
            } else {
                // User is not logged in
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already logged out");
            }
            // Logout the user
        } else {
            // User is not logged in
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }
    }

    public ResponseEntity<String> forgotPassword(Voter voter) {
        // Check if the user exists
        Voter existingVoter = voterDAO.getVoterByEmail(voter.getEmail());
        if (existingVoter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the provided email does not exist.");
        }

        // Use voterDAO to send the OTP and email
        try {
            voterDAO.sendRegistrationConfirmationEmail(existingVoter);
            // Return a success response
            return ResponseEntity.ok("OTP has been sent to your email. Use the token for further verification.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP to email.");
        }
    }

    public ResponseEntity<String> resetPassword(Voter voter) {
        // Check if the user exists
        Voter existingVoter = voterDAO.getVoterByEmail(voter.getEmail());
        if (existingVoter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the provided email does not exist.");
        }

        voterDAO.passwordReset(voter.getPassword(), existingVoter);

        // Return a success response
        return ResponseEntity.ok("Password has been successfully reset.");
    }
}
