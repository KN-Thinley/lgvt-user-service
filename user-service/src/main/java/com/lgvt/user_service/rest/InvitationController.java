package com.lgvt.user_service.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lgvt.user_service.entity.Invitation;
import com.lgvt.user_service.service.InvitationService;

@RestController
@RequestMapping("/api")
public class InvitationController {
    @Autowired
    private InvitationService invitationService;

    @PostMapping("/super-admin/invitation")
    public ResponseEntity<String> createInvitation(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        // Save the invitation
        invitationService.saveInvitation(email);

        return ResponseEntity.ok("Successfully invited the user");
    }

    // invitation API --- Generate code and send mail*
    // Resent API
    // Verify Invitation --- expired --- valid --- resend email or invitationId
    // Delete User --- should it be diable or what --- directly delete the
    // invitation if not accepted and even if accepted delete the invitation, but
    // decide how to handle the deletion from the user table, think should not
    // delete that part
    // Get Users --- in super admin Controller --- do i have to link the use and all
    // see it by adding a userId in invitation or something

    // Admin Account Setup -- Setup the isUsed to true -- valide the request through
    // email or invitationId
}
