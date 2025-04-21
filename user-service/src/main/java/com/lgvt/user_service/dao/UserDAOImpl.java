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
                    "SELECT v FROM User v WHERE v.email = :email", User.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no voter is found with the given emails
        }
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
}
