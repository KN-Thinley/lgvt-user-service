package com.lgvt.user_service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public String sendRegistrationConfirmationEmail(Voter voter) {
        // Create a secure token
        SecureToken secureToken = secureTokenService.createToken(voter);

        // Save the secure token
        secureTokenService.saveSecureToken(secureToken);

        System.out.println("Secure token: " + secureToken.getToken());

        // Prepare the email context
        AccountEmailContext emailContext = new AccountEmailContext();
        emailContext.init(voter);
        emailContext.setToken(secureToken.getToken());
        emailContext.setOtp(secureToken.getOtp());
        emailContext.buildVerificationUrl(baseUrl, secureToken.getToken());

        // Send the email
        try {
            emailService.sendMail(emailContext);
            return secureToken.getToken();
        } catch (Exception e) {
            // Log the error for debugging purposes
            System.err.println("Failed to send registration confirmation email: " + e.getMessage());
            e.printStackTrace();

            // Throw a custom exception to indicate email sending failure
            throw new RuntimeException("Failed to send registration confirmation email. Please try again later.");
        }
    }

    @Override
    public String sendLoginMFAEmail(Voter voter) {
        // Create a secure token
        SecureToken secureToken = secureTokenService.createToken(voter);

        // Save the secure token
        secureTokenService.saveSecureToken(secureToken);

        System.out.println("Secure token: " + secureToken.getToken());

        // Prepare the email context
        AccountEmailContext emailContext = new AccountEmailContext();
        emailContext.init(voter);
        emailContext.setToken(secureToken.getToken());
        emailContext.setOtp(secureToken.getOtp());
        emailContext.buildVerificationUrl(baseUrl, secureToken.getToken());

        // Send the email
        try {
            emailService.sendMFAMail(emailContext);
            return secureToken.getToken();
        } catch (Exception e) {
            // Log the error for debugging purposes
            System.err.println("Failed to send registration confirmation email: " + e.getMessage());
            e.printStackTrace();

            // Throw a custom exception to indicate email sending failure
            throw new RuntimeException("Failed to send registration confirmation email. Please try again later.");
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

    @Override
    public Voter getVoterByEmail(String email) {
        try {
            TypedQuery<Voter> query = entityManager.createQuery(
                    "SELECT v FROM Voter v WHERE v.email = :email", Voter.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no voter is found with the given emails
        }
    }

    @Override
    public boolean checkIfPasswordMatches(String password, String oldPassword) {
        boolean isPasswordMatch = passwordEncoder.matches(password, oldPassword);
        return isPasswordMatch;
    }

    @Override
    @Transactional
    public void logoutVoter(Voter voter) {
        voter.setLogged_in(false);
        entityManager.merge(voter);
    }

    @Override
    @Transactional
    public void passwordReset(String password, Voter voter) {
        // Encrypt the new password
        String encryptedPassword = passwordEncoder.encode(password);

        // // Update the voter's password
        voter.setPassword(encryptedPassword);

        // Save the updated voter entity
        entityManager.merge(voter);
    }
}
