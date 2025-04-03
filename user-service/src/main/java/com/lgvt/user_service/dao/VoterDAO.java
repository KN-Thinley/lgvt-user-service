package com.lgvt.user_service.dao;

import com.lgvt.user_service.entity.Voter;

public interface VoterDAO {
    Voter saveVoter(Voter voter);

    boolean checkIfUserExists(String email);

    void sendRegistrationConfirmationEmail(Voter voter);

    Voter changeVoterStatus(int id);

    Voter getVoterByEmail(String email);

    boolean checkIfPasswordMatches(String password, String oldPassword);

    void logoutVoter(Voter voter);

    void passwordReset(String password, Voter voter);
}
