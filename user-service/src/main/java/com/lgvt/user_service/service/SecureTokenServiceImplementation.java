package com.lgvt.user_service.service;

import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.lgvt.user_service.dao.SecureTokenDAO;
import com.lgvt.user_service.dao.VoterDAO;
import com.lgvt.user_service.entity.SecureToken;
import com.lgvt.user_service.entity.Voter;

@Service
public class SecureTokenServiceImplementation implements SecureTokenService {
    private static BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(12);

    @Value("2800")
    private int tokenValidatyInSeconds;

    @Autowired
    private SecureTokenDAO secureTokenDAO;
    @Autowired
    private VoterDAO voterDAO;

    @Override
    public SecureToken createToken(Voter voter) {
        String tokenValue = new String(Base64.getEncoder().encode(DEFAULT_TOKEN_GENERATOR.generateKey()));
        int otpValue = (int) (Math.random() * 900000) + 10000;

        SecureToken secureToken = new SecureToken();
        secureToken.setToken(tokenValue);
        secureToken.setOtp(otpValue);
        secureToken.setExpireAt(LocalDateTime.now().plusSeconds(tokenValidatyInSeconds));
        secureToken.setVoter(voter);
        this.saveSecureToken(secureToken);
        return secureToken;
    }

    @Override
    public void saveSecureToken(SecureToken secureToken) {
        secureTokenDAO.save(secureToken);
    }

    @Override
    public SecureToken findByToken(String token) {
        return secureTokenDAO.findByToken(token);
    }

    @Override
    public void removeToken(String token) {
        secureTokenDAO.removeByToken(token);
    }

    @Override
    public boolean verifyOtp(int otp, String token) {
        SecureToken secureToken = secureTokenDAO.findByToken(token);

        if (secureToken == null) {
            throw new RuntimeException("Invalid token. Please request a new one.");
        }

        // Check if the token is expired
        if (secureToken.getExpireAt().isBefore(LocalDateTime.now())) {
            secureTokenDAO.removeByToken(token);
            throw new RuntimeException("Token has expired. Please request a new one.");
        }

        // Verify OTP
        if (secureToken.getOtp() == otp) {
            return true;
        } else {
            throw new RuntimeException("Invalid OTP. Please try again.");
        }
    }

    @Override
    public void changeVoterStatus(String token) {
        SecureToken secureToken = secureTokenDAO.findByToken(token);
        int voter_id = secureToken.getVoter().getId();
        Voter voter = voterDAO.changeVoterStatus(voter_id);
    }
}
