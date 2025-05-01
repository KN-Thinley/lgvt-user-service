package com.lgvt.user_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.lgvt.user_service.Response.LoginResponse;
import com.lgvt.user_service.Response.LoginUserInfo;
import com.lgvt.user_service.dao.SecureTokenDAO;
import com.lgvt.user_service.dao.UserDAO;
import com.lgvt.user_service.dao.VoterDAO;
import com.lgvt.user_service.entity.User;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.security.CustomDetailsService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.Data;

@Service
@Data
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private VoterDAO voterDAO;
    @Autowired
    private SecureTokenDAO secureTokenDAO;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomDetailsService customUserDetailsService;

    @Override
    public ResponseEntity<String> saveUser(User user) {
        try {
            // Check if the user already exists by email
            if (userDAO.userExistsByEmail(user.getEmail())) {
                return new ResponseEntity<>("User already exists with the provided email", HttpStatus.CONFLICT);
            }

            // Save the user if they don't exist
            User savedUser = userDAO.saveUser(user);
            if (savedUser != null) {
                return new ResponseEntity<>("User saved successfully", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to save user", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<LoginResponse> login(User user, HttpServletResponse response) {
        // Fetch the user by email
        User existingUser = userDAO.getUserByEmail(user.getEmail());

        if (existingUser != null) {
            // Authenticate the user
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                // Update the last login time
                existingUser.setLastLogin(LocalDateTime.now());
                userDAO.saveUserWithoutPasswordEncryption(existingUser); // Save the updated user

                String token = voterDAO.sendLoginMFAEmail(existingUser);

                LoginUserInfo userInfo = new LoginUserInfo(
                        existingUser.getId(),
                        existingUser.getEmail(),
                        existingUser.getName(),
                        existingUser.getRole().toString());

                // Return success response
                return ResponseEntity.ok(new LoginResponse(
                        "Successful Send A MFA Email",
                        token,
                        false,
                        "redirect_to_mfa",
                        userInfo));
            } else {
                // Password is incorrect
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(
                        "Incorrect password",
                        null,
                        false,
                        "retry_login", null));
            }
        } else {
            // User does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginResponse(
                    "User does not exist",
                    null,
                    false,
                    "register_user", null));
        }
    }

    public ResponseEntity<String> logout(String email, HttpServletResponse response) {

        User existingVoter = userDAO.getUserByEmail(email);

        if (existingVoter != null) {
            // ་Cookie Clearing
            Cookie jwtCookie = new Cookie("JWT-TOKEN", null);
            jwtCookie.setMaxAge(0);
            response.addCookie(jwtCookie);
            return ResponseEntity.ok().build();

        } else {
            // User is not logged in
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        }
    }

    public ResponseEntity<String> updatePassword(String password, String email) {
        User existingUser = userDAO.getUserByEmail(email);

        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        userDAO.passwordReset(password, existingUser);
        return ResponseEntity.ok("Password has been successfully updated.");
    }

    public ResponseEntity<Map<String, Object>> getUserInfoByEmail(String email) {
        // Fetch voter by email
        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            // Return 404 if voter not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Voter not found"));
        }

        // Prepare selective fields to return
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());

        // Return the response
        return ResponseEntity.ok(userInfo);
    }

    @Override
    public Map<String, Long> getSystemStatistics() {
        // Fetch data from DAO methods
        long totalVoters = voterDAO.getTotalVoterCount();
        long totalVotersToday = voterDAO.getTotalVotersRegisteredToday();
        long totalUsers = userDAO.getTotalUserCount();

        // Combine results into a map
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("totalVoters", totalVoters);
        statistics.put("totalVotersToday", totalVotersToday);
        statistics.put("totalUsers", totalUsers);

        return statistics;
    }

    @Override
    public List<Map<String, Object>> getAllVoters(String adminEmail) {
        // Fetch the admin user by email
        User admin = userDAO.getUserByEmail(adminEmail);
        if (admin == null) {
            throw new IllegalArgumentException("Admin not found with email: " + adminEmail);
        }

        // Get the dzongkhag and gewog of the admin
        String adminDzongkhag = admin.getDzongkhag();
        String adminGewog = admin.getGewog();

        // Fetch voters based on the admin's dzongkhag and gewog
        List<Voter> voters = voterDAO.findByDzongkhagAndGewog(adminDzongkhag, adminGewog);

        // Prepare a list to hold the voter details
        List<Map<String, Object>> voterDetailsList = new ArrayList<>();

        for (Voter voter : voters) {
            // Combine dzongkhag and gewog into the "dzongkhag-gewog" format
            String location = voter.getDzongkhag() + "-" + voter.getGewog();

            // Create a map to hold the voter details
            Map<String, Object> voterDetails = new HashMap<>();
            voterDetails.put("id", voter.getId());
            voterDetails.put("cid", voter.getCid());
            voterDetails.put("name", voter.getName());
            voterDetails.put("age", calculateAge(voter.getDob()));
            voterDetails.put("location", location);

            // Add the map to the list
            voterDetailsList.add(voterDetails);
        }

        return voterDetailsList;
    }

    public int calculateAge(LocalDate dob) {
        if (dob == null) {
            return 0; // Return 0 if the date of birth is null
        }
        return Period.between(dob, LocalDate.now()).getYears();
    }

    @Override
    @Transactional
    public void deleteVoterById(int id) {
        // Check if the voter exists
        Voter voter = voterDAO.findById(id);
        if (voter == null) {
            throw new IllegalArgumentException("Voter not found with ID: " + id);
        }

        // Delete all tokens related to the voter
        // secureTokenDAO.deleteByVoterId(id);

        // Delete the voter
        voterDAO.delete(voter);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAdminInfo(String adminEmail) {
        // Fetch the admin user by email
        User admin = userDAO.getUserByEmail(adminEmail);

        if (admin == null) {
            // Return 404 if admin not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Admin not found with email: " + adminEmail));
        }

        // Prepare selective fields to return
        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("name", admin.getName());
        adminInfo.put("email", admin.getEmail());
        adminInfo.put("number", admin.getPhone());
        adminInfo.put("dzongkhag", admin.getDzongkhag());
        adminInfo.put("gewog", admin.getGewog());

        // Return the response
        return ResponseEntity.ok(adminInfo);
    }

    @Override
    public ResponseEntity<Map<String, Long>> getVoterStatistics() {
        // Fetch the total number of voters
        long totalVoters = voterDAO.getTotalVoterCount();

        // Fetch the number of voters who registered today
        long votersRegisteredToday = voterDAO.getTotalVotersRegisteredToday();

        // Prepare the response
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("totalVoters", totalVoters);
        statistics.put("votersRegisteredToday", votersRegisteredToday);

        return ResponseEntity.ok(statistics);
    }
}