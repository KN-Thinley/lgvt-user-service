package com.lgvt.user_service.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lgvt.user_service.Response.LoginResponse;
import com.lgvt.user_service.entity.User;
import com.lgvt.user_service.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class UserRestcontroller {
    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        return userService.saveUser(user);

    }

    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody User user, HttpServletResponse response) {
        return userService.login(user, response);
    }
}