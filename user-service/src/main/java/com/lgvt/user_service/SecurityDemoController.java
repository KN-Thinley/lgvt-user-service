package com.lgvt.user_service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class SecurityDemoController {
    @RequestMapping("/public")
    public String publicEndPoint() {
        return "Public EndPoint";
    }

    @RequestMapping("/admin")
    public String adminEndPoint() {
        return "Admin EndPoint";
    }

    @RequestMapping("/user")
    public String userEndPoint() {
        return "User EndPoint";
    }
}