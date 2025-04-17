package com.lgvt.user_service.utils;

import org.springframework.web.util.UriComponentsBuilder;

import com.lgvt.user_service.entity.Voter;

public class AccountEmailContext extends AbstractEmailContext {

    private String token;

    @Override
    public <T> void init(T context) {
        Voter user = (Voter) context;

        put("name", user.getName());
        setSubject("Complete Your Registration by Entering the Code");
        setFrom("ryoutamikasa@gmail.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, String token) {
        final String url = UriComponentsBuilder.fromUriString(baseURL)
                .path("/register/verify").queryParam("token", token).toUriString();
        put("verificationURL", url);
    }

}
