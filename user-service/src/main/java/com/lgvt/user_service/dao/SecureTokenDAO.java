package com.lgvt.user_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lgvt.user_service.entity.SecureToken;

import jakarta.transaction.Transactional;

public interface SecureTokenDAO extends JpaRepository<SecureToken, Long> {
    SecureToken findByToken(final String token);

    @Transactional
    Long removeByToken(final String token);
}
