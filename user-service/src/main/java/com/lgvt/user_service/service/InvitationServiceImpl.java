package com.lgvt.user_service.service;

import com.lgvt.user_service.dao.InvitationDAO;
import com.lgvt.user_service.dao.UserDAO;
import com.lgvt.user_service.entity.Invitation;
import com.lgvt.user_service.entity.InvitationStatus;
import com.lgvt.user_service.entity.User;
import com.lgvt.user_service.entity.UserStatus;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationDAO invitationDAO;
    @Autowired
    private UserDAO userDAO;

    private static final int CODE_LENGTH = 8;

    @Override
    public Invitation saveInvitation(String email) {
        // Check for an existing invitation
        Invitation existingInvitation = invitationDAO.findByEmail(email);
        if (existingInvitation != null) {
            if (existingInvitation.getStatus() == InvitationStatus.ARCHIVED) {
                // Reuse the archived invitation
                existingInvitation.setStatus(InvitationStatus.PENDING);
                existingInvitation.setCode(generateInvitationCode());
                existingInvitation.setExpiresAt(LocalDateTime.now().plusDays(1)); // Reset expiration time
                Invitation updatedInvitation = invitationDAO.saveInvitation(existingInvitation);

                // Send the invitation email
                invitationDAO.sendInvitationEmail(updatedInvitation);

                return updatedInvitation;
            } else {
                throw new IllegalArgumentException("An invitation already exists for the provided email: " + email);
            }
        }

        // Create a new Invitation object
        Invitation invitation = new Invitation();
        invitation.setEmail(email);
        invitation.setCode(generateInvitationCode());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(1)); // Set expiration time

        // Save the invitation using the DAO
        Invitation savedInvitation = invitationDAO.saveInvitation(invitation);

        // Send the invitation email
        invitationDAO.sendInvitationEmail(savedInvitation);

        return savedInvitation;
    }

    @Override
    public Invitation resendInvitation(String email) {
        // Find the existing invitation by email
        Invitation existingInvitation = invitationDAO.findByEmail(email);
        if (existingInvitation == null) {
            throw new IllegalArgumentException("No invitation found for the provided email: " + email);
        }

        // Check if the invitation is archived
        if (existingInvitation.getStatus() == InvitationStatus.ARCHIVED) {
            throw new IllegalArgumentException("Cannot resend an archived invitation");
        }

        // Check if the token has expired
        if (existingInvitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Generate a new token and update the expiration time
            String newCode = generateInvitationCode();
            existingInvitation.setCode(newCode);
            existingInvitation.setExpiresAt(LocalDateTime.now().plusDays(1)); // Set new expiration time

            // Save the updated invitation
            invitationDAO.saveInvitation(existingInvitation);
        }

        // Resend the invitation email
        invitationDAO.sendInvitationEmail(existingInvitation);

        return existingInvitation;
    }

    @Override
    public Long verifyInvitation(String email, String code) {
        // Find the invitation by email
        Invitation invitation = invitationDAO.findByEmail(email);
        if (invitation == null) {
            throw new IllegalArgumentException("No invitation found for the provided email: " + email);
        }

        // Check if the invitation is already accepted
        if (invitation.getStatus() == InvitationStatus.ACCEPTED) {
            throw new IllegalArgumentException("The invitation has already been accepted");
        }

        // Check if the invitation has expired
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("The invitation has expired");
        }

        // Check if the code matches
        if (!invitation.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid invitation code");
        }

        // Update the status to ACCEPTED and mark as used
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setUsed(true);
        invitationDAO.saveInvitation(invitation);

        // Return the ID of the invitation
        return invitation.getId();
    }

    @Override
    public void registerAdmin(Long invitationId, User user) {
        // Find the invitation by ID
        Invitation invitation = invitationDAO.findById(invitationId);
        if (invitation == null) {
            throw new IllegalArgumentException("No invitation found for the provided ID: " + invitationId);
        }

        // Check if the invitation is already accepted
        if (invitation.getStatus() != InvitationStatus.ACCEPTED) {
            throw new IllegalArgumentException("The invitation must be accepted before registering the user");
        }

        // Check if the user already exists in the database
        User existingUser = userDAO.findByEmail(invitation.getEmail());
        if (existingUser != null) {
            // If the user exists, update their credentials and enable them
            existingUser.setName(user.getName());
            existingUser.setPhone(user.getPhone());
            existingUser.setDzongkhag(user.getDzongkhag());
            existingUser.setGewog(user.getGewog());
            existingUser.setPassword(user.getPassword());
            existingUser.setStatus(UserStatus.ACTIVE); // Reactivate the user
            userDAO.saveUser(existingUser);
        } else {
            // If the user does not exist, set the email from the invitation to the user
            // object
            user.setEmail(invitation.getEmail());

            // Register the user using the UserDAO
            userDAO.saveUser(user);
        }
    }

    @Override
    public List<Map<String, Object>> getInvitationAndUserDetails() {
        // Fetch all non-archived rows from the Invitation table
        List<Invitation> invitations = invitationDAO.findAllNonArchived();

        // Prepare a list to hold the combined details
        List<Map<String, Object>> detailsList = new ArrayList<>();

        for (Invitation invitation : invitations) {
            // Fetch the user details using the email from the Invitation table
            User user = userDAO.findByEmail(invitation.getEmail());

            // Combine the details into a map
            Map<String, Object> details = new HashMap<>();
            details.put("name", user != null ? user.getName() : "N/A");
            details.put("email", invitation.getEmail());
            details.put("last_login", user != null ? formatLastLogin(user.getLastLogin()) : "Never");
            details.put("status", invitation.getStatus());

            // Add the map to the list
            detailsList.add(details);
        }

        return detailsList;
    }

    private String formatLastLogin(LocalDateTime lastLogin) {
        if (lastLogin == null) {
            return "Never";
        }

        Duration duration = Duration.between(lastLogin, LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (seconds < 3600) {
            return (seconds / 60) + " minutes ago";
        } else if (seconds < 86400) {
            return (seconds / 3600) + " hours ago";
        } else {
            return (seconds / 86400) + " days ago";
        }
    }

    private String generateInvitationCode() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder(CODE_LENGTH);
        String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(characterSet.length());
            codeBuilder.append(characterSet.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    @Override
    public void deleteUserOrInvitation(String email) {
        // Check if the user exists in the User table
        User user = userDAO.findByEmail(email);
        if (user != null) {
            // If the user exists, disable them
            user.setStatus(UserStatus.DISABLED);
            userDAO.saveUser(user);
        }

        // Check if the invitation exists and archive it
        Invitation invitation = invitationDAO.findByEmail(email);
        if (invitation != null) {
            // Mark the invitation as ARCHIVED
            invitation.setStatus(InvitationStatus.ARCHIVED);
            invitationDAO.saveInvitation(invitation);
        } else if (user == null) {
            // If neither user nor invitation exists, throw an exception
            throw new IllegalArgumentException("No user or invitation found for the provided email: " + email);
        }
    }
}