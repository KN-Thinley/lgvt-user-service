package com.lgvt.user_service.service;

import org.springframework.http.ResponseEntity;

import com.lgvt.user_service.Response.LoginResponse;
import com.lgvt.user_service.entity.User;

import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    ResponseEntity<String> saveUser(User user);

    ResponseEntity<LoginResponse> login(User user, HttpServletResponse response);
}
