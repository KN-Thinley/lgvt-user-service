package com.lgvt.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lgvt.user_service.dao.VoterDAO;
import com.lgvt.user_service.entity.Voter;

import jakarta.transaction.Transactional;

@Service
public class VoterServiceImpl implements VoterService {
    private VoterDAO voterDAO;

    @Autowired
    public VoterServiceImpl(VoterDAO voterDAO) {
        this.voterDAO = voterDAO;
    }

    @Override
    @Transactional
    public Voter saveVoter(Voter voter) {
        return voterDAO.saveVoter(voter);
    }

}
