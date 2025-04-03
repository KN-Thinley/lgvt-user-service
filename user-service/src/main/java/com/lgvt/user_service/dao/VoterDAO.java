package com.lgvt.user_service.dao;

import com.lgvt.user_service.entity.Voter;

public interface VoterDAO {
    Voter saveVoter(Voter voter);

    boolean checkIfUserExists(String email);

    void sendRegistrationConfirmationEmail(Voter voter);

    Voter changeVoterStatus(int id);
}
