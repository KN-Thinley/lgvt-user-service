package com.lgvt.user_service.utils;

import org.springframework.web.util.UriComponentsBuilder;

import com.lgvt.user_service.entity.Invitation;

public class InvitationEmailContext extends AbstractEmailContext {

    private String token;
    private String expiresAt;

    @Override
    public <T> void init(T context) {
        Invitation invitation = (Invitation) context;

        setSubject("You're Invited to Join Our Platform");
        setFrom("noreply@lgvt.com");
        setTo(invitation.getEmail());
    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
        put("expiresAt", expiresAt);
    }

    public void buildInvitationUrl(final String baseURL, String token) {
        final String url = UriComponentsBuilder.fromUriString(baseURL)
                .path("/invitation/accept").queryParam("token", token).toUriString();
        put("invitationURL", url);
    }
}