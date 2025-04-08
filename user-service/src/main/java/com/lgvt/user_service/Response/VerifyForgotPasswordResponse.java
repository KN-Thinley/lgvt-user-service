package com.lgvt.user_service.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyForgotPasswordResponse {
    private String message;
    private String token;
}
