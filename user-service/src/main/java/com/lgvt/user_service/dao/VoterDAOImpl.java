package com.lgvt.user_service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lgvt.user_service.entity.Voter;

import jakarta.persistence.EntityManager;

@Repository
public class VoterDAOImpl implements VoterDAO {
    private EntityManager entityManager;

    @Autowired
    public VoterDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Voter saveVoter(Voter voter) {
        return entityManager.merge(voter);
    }
}
