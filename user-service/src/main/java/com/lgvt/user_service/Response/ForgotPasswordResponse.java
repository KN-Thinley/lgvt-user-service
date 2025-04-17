package com.lgvt.user_service.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForgotPasswordResponse {
    private String message;
    private String token;
}
