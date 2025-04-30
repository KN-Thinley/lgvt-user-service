package com.lgvt.user_service.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lgvt.user_service.entity.Invitation;
import com.lgvt.user_service.entity.Role;
import com.lgvt.user_service.entity.User;
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

    @PostMapping("/super-admin/invitation/resent")
    public ResponseEntity<String> resendInvitation(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            // Save and send the invitation
            invitationService.resendInvitation(email);
            return ResponseEntity.ok("Invitation resent successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while resending the invitation");
        }
    }

    @PostMapping("/super-admin/invitation/verify")
    public ResponseEntity<Object> verifyInvitation(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");

        if (email == null || email.isBlank() || code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body("Email and code are required");
        }

        try {
            Long invitationId = invitationService.verifyInvitation(email, code);
            return ResponseEntity.ok(Map.of(
                    "message", "Invitation verified successfully",
                    "invitationId", invitationId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An error occurred while verifying the invitation"));
        }
    }

    @PostMapping("/super-admin/invitation/register")
    public ResponseEntity<Object> registerAdmin(
            @RequestParam Long invitationId,
            @RequestBody Map<String, String> payload) {

        // Extract fields from the payload
        String name = payload.get("name");
        String phone = payload.get("phone");
        String dzongkhag = payload.get("dzongkhag");
        String gewog = payload.get("gewog");
        String password = payload.get("password");

        // Validate required fields
        if (name == null || name.isBlank() ||
                phone == null || phone.isBlank() ||
                dzongkhag == null || dzongkhag.isBlank() ||
                gewog == null || gewog.isBlank() ||
                password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("All fields (name, phone, dzongkhag, gewog, password) are required");
        }

        try {
            // Create a new User object and set the fields
            User user = new User();
            user.setName(name);
            user.setPhone(phone);
            user.setDzongkhag(dzongkhag);
            user.setGewog(gewog);
            user.setPassword(password);
            user.setRole(Role.ADMIN); // Set role to ADMIN

            // Call the service method to register the admin
            invitationService.registerAdmin(invitationId, user);

            return ResponseEntity.ok("Admin registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while registering the admin");
        }
    }

    @GetMapping("/super-admin/admins")
    public ResponseEntity<List<Map<String, Object>>> getInvitationAndUserDetails() {
        try {
            List<Map<String, Object>> details = invitationService.getInvitationAndUserDetails();
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/super-admin/admin")
    public ResponseEntity<String> deleteUserOrInvitation(@RequestParam String email) {
        try {
            invitationService.deleteUserOrInvitation(email);
            return ResponseEntity.ok("User or invitation deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while deleting the user or invitation");
        }
    }
}
