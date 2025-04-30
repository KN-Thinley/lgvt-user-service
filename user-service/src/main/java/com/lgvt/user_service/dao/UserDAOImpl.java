package com.lgvt.user_service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.lgvt.user_service.entity.User;
import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.service.EmailService;
import com.lgvt.user_service.service.SecureTokenService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class UserDAOImpl implements UserDAO {
    private EntityManager entityManager;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public UserDAOImpl(EntityManager entityManager, BCryptPasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE LOWER(u.email) = :email", User.class);
            query.setParameter("email", email.trim().toLowerCase());
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no user is found with the given email
        }
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return entityManager.merge(user);
    }

    @Override
    @Transactional
    public User saveUserWithoutPasswordEncryption(User user) {
        return entityManager.merge(user);
    }

    @Override
    public boolean userExistsByEmail(String email) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false; // Return false in case of any unexpected exception
        }
    }

    @Override
    @Transactional
    public void passwordReset(String password, User user) {
        // Encrypt the new password
        String encryptedPassword = passwordEncoder.encode(password);

        // // Update the voter's password
        user.setPassword(encryptedPassword);

        // Save the updated voter entity
        entityManager.merge(user);
    }

    @Override
    public long getTotalUserCount() {
        String query = "SELECT COUNT(u) FROM User u";
        return entityManager.createQuery(query, Long.class).getSingleResult();
    }

    public User findByEmail(String email) {
        String query = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> typedQuery = entityManager.createQuery(query, User.class);
        typedQuery.setParameter("email", email);
        return typedQuery.getResultStream().findFirst().orElse(null);
    }

}
