package com.lgvt.user_service.dao;

import com.lgvt.user_service.entity.User;
import com.lgvt.user_service.entity.Voter;

public interface UserDAO {
    User getUserByEmail(String email);

    User saveUser(User user);

    boolean userExistsById(int id);

    User saveUserWithoutPasswordEncryption(User user);

    boolean userExistsByEmail(String email);

    void passwordReset(String password, User user);

    long getTotalUserCount();

    // Admin view
    User findByEmail(String email);

    User save(User user);
}