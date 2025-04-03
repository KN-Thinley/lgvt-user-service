package com.lgvt.user_service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.lgvt.user_service.entity.SecureToken;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.service.EmailService;
import com.lgvt.user_service.service.SecureTokenService;
import com.lgvt.user_service.utils.AccountEmailContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class VoterDAOImpl implements VoterDAO {
    private EntityManager entityManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private EmailService emailService;
    private SecureTokenService secureTokenService;

    @Value("${app.base.url}")
    private String baseUrl;

    @Autowired
    @Lazy
    public VoterDAOImpl(EntityManager entityManager, BCryptPasswordEncoder passwordEncoder,
            SecureTokenService secureTokenService, EmailService emailService) {
        this.secureTokenService = secureTokenService;
        this.emailService = emailService;
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Voter saveVoter(Voter voter) {
        voter.setPassword(passwordEncoder.encode(voter.getPassword()));
        return entityManager.merge(voter);
    }

    @Override
    public boolean checkIfUserExists(String email) {
        try {
            TypedQuery<Voter> query = entityManager.createQuery("SELECT u FROM Voter u WHERE u.email = :email",
                    Voter.class);
            query.setParameter("email", email);
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public void sendRegistrationConfirmationEmail(Voter voter) {
        // Create a secure token
        SecureToken secureToken = secureTokenService.createToken(voter);

        // Save the secure token
        secureTokenService.saveSecureToken(secureToken);

        // Prepare the email context
        AccountEmailContext emailContext = new AccountEmailContext();
        emailContext.init(voter);
        emailContext.setToken(secureToken.getToken());
        emailContext.setOtp(secureToken.getOtp());
        emailContext.buildVerificationUrl(baseUrl, secureToken.getToken());

        // Send the email
        try {
            emailService.sendMail(emailContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public Voter changeVoterStatus(int id) {
        Voter existingVoter = entityManager.find(Voter.class, id);
        if (existingVoter != null) {
            existingVoter.setVerified(true);
            existingVoter.setLogged_in(true);
            return entityManager.merge(existingVoter);
        }
        return null;
    }
}
