package com.lgvt.user_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lgvt.user_service.entity.SecureToken;

public interface SecureTokenDAO extends JpaRepository<SecureToken, Long> {
    SecureToken findByToken(final String token);

    Long deleteByToken(final String token);

    @Modifying
    @Query("DELETE FROM SecureToken st WHERE st.voter.id = :voterId")
    void deleteTokensByVoterId(@Param("voterId") int voterId);
}
