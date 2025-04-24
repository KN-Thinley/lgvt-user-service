package com.lgvt.user_service.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserInfo {
    private Integer id; // User ID
    private String email; // User email
    private String name; // User name
    private String role; // User role
}
