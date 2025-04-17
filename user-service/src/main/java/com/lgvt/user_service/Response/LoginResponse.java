package com.lgvt.user_service.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String message; // Message for the frontend
    private String token; // JWT token (if login is successful)
    private boolean success; // Indicates if the login was successful
    private String action; // Suggested action for the frontend (e.g., "redirect_to_mfa", "retry_login")
}
