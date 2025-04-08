package com.lgvt.user_service.service;

import com.lgvt.user_service.entity.SecureToken;
import com.lgvt.user_service.entity.Voter;

public interface SecureTokenService {
    SecureToken createToken(Voter voter);

    void saveSecureToken(SecureToken secureToken);

    SecureToken findByToken(String token);

    void removeToken(String token);

    boolean verifyOtp(int otp, String token);

    void changeVoterStatus(String token);

    void changeVoterLoginStatus(String token);
}
