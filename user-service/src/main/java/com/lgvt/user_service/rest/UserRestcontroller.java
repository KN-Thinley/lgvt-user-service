package com.lgvt.user_service.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lgvt.user_service.Response.LoginResponse;
import com.lgvt.user_service.entity.User;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.service.UserService;
import com.lgvt.user_service.service.VoterService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class UserRestcontroller {
    @Autowired
    private UserService userService;

    @Autowired
    private VoterService voterService;

    @PostMapping("/super-user/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        return userService.saveUser(user);

    }

    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody User user, HttpServletResponse response) {
        return userService.login(user, response);
    }

    // @PostMapping("/user/logout")
    // public ResponseEntity<String> logoutUser(Authentication authentication,
    // HttpServletResponse response) {
    // String email = authentication.getName();
    // return userService.logout(email, response);
    // }

    @PostMapping("/user/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String password, Authentication authentication) {
        String email = authentication.getName();
        return userService.updatePassword(password, email);
    }

    @GetMapping("/super-admin/info")
    public ResponseEntity<Map<String, Object>> getVoterInfo(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserInfoByEmail(email);
    }

    @GetMapping("/super-admin/statistics")
    public ResponseEntity<Map<String, Long>> getSystemStatistics() {
        Map<String, Long> statistics = userService.getSystemStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/admin/voters")
    public ResponseEntity<List<Map<String, Object>>> getAllVoters(Authentication authentication) {
        // Get the admin's email from the authentication object
        String email = authentication.getName();

        // Pass the admin's email to the service method
        List<Map<String, Object>> voters = userService.getAllVoters(email);

        return ResponseEntity.ok(voters);
    }

    @DeleteMapping("/admin/voter/{id}")
    public ResponseEntity<String> deleteVoter(@PathVariable int id) {
        try {
            System.out.println("Deleting voter with ID: " + id);
            userService.deleteVoterById(id);
            return ResponseEntity.ok("Voter deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the voter");
        }
    }
}