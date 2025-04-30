package com.lgvt.user_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.lgvt.user_service.Response.LoginResponse;
import com.lgvt.user_service.entity.User;
import com.lgvt.user_service.entity.Voter;

import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    ResponseEntity<String> saveUser(User user);

    ResponseEntity<LoginResponse> login(User user, HttpServletResponse response);

    ResponseEntity<String> logout(String email, HttpServletResponse response);

    public ResponseEntity<String> updatePassword(String password, String email);

    ResponseEntity<Map<String, Object>> getUserInfoByEmail(String email);

    Map<String, Long> getSystemStatistics();

    List<Map<String, Object>> getAllVoters();
}
