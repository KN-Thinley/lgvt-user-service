package com.lgvt.user_service.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String message;
    private String token;
    private boolean success;
    private String action;
    private LoginUserInfo userInfo; // User information
}
