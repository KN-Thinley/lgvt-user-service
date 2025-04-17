package com.lgvt.user_service.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.lgvt.user_service.entity.Voter;

public class CustomUserDetails implements UserDetails {
    private Voter voter;

    public CustomUserDetails(Voter voter) {
        this.voter = voter;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return voter.getPassword();
    }

    @Override
    public String getUsername() {
        return voter.getEmail();
    }

}
