package com.lgvt.user_service.dao;

import com.lgvt.user_service.entity.User;
import com.lgvt.user_service.entity.Voter;

public interface UserDAO {
    User getUserByEmail(String email);
}
