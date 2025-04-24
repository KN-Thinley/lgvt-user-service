package com.lgvt.user_service.dao;

import com.lgvt.user_service.entity.GeneralUser;
import com.lgvt.user_service.entity.SecureToken;
import com.lgvt.user_service.entity.Voter;

public interface VoterDAO {
    Voter saveVoter(Voter voter);

    boolean checkIfUserExists(String email);

    String sendRegistrationConfirmationEmail(Voter voter);

    String sendLoginMFAEmail(GeneralUser user);

    String sendForgotPasswordEmail(GeneralUser user);

    Voter changeVoterStatus(int id);

    Voter changeVoterLoginStatus(int id);

    Voter getVoterByEmail(String email);

    boolean checkIfPasswordMatches(String password, String oldPassword);

    void logoutVoter(Voter voter);

    void passwordReset(String password, Voter voter);

    String resentOTP(SecureToken secureToken, String type);

    long getTotalVoterCount();

    long getTotalVotersRegisteredToday();
}
