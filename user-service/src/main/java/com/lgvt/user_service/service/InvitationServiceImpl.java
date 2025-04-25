package com.lgvt.user_service.service;

import com.lgvt.user_service.dao.InvitationDAO;
import com.lgvt.user_service.entity.Invitation;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationDAO invitationDAO;

    private static final int CODE_LENGTH = 8;

    @Override
    public Invitation saveInvitation(String email) {
        // Check for duplicate invitation by email
        Invitation existingInvitation = invitationDAO.findByEmail(email);
        if (existingInvitation != null) {
            throw new IllegalArgumentException("An invitation already exists for the provided email: " + email);
        }

        // Create a new Invitation object
        Invitation invitation = new Invitation();

        // Set the email
        invitation.setEmail(email);

        // Generate an alphanumeric invitation code
        String generatedCode = generateInvitationCode();
        invitation.setCode(generatedCode);

        // Save the invitation using the DAO
        Invitation savedInvitation = invitationDAO.saveInvitation(invitation);

        // Send the invitation email
        invitationDAO.sendInvitationEmail(savedInvitation);

        return savedInvitation;
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
}