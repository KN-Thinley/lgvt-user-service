package com.lgvt.user_service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.lgvt.user_service.entity.Voter;

import jakarta.persistence.EntityManager;

@Repository
public class VoterDAOImpl implements VoterDAO {
    private EntityManager entityManager;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public VoterDAOImpl(EntityManager entityManager, BCryptPasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Voter saveVoter(Voter voter) {
        voter.setPassword(passwordEncoder.encode(voter.getPassword()));
        return entityManager.merge(voter);
    }
}
