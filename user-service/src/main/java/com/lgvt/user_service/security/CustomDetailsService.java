package com.lgvt.user_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lgvt.user_service.dao.UserDAO;
import com.lgvt.user_service.dao.VoterDAO;
import com.lgvt.user_service.entity.User;
import com.lgvt.user_service.entity.Voter;

@Service
public class CustomDetailsService implements UserDetailsService {
    @Autowired
    private VoterDAO voterDAO;
    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Voter
        Voter voter = voterDAO.getVoterByEmail(email);
        if (voter != null) {
            return new CustomUserDetails(voter);
        }

        User user = userDAO.getUserByEmail(email);
        if (user != null) {
            return new CustomUserDetails(user);
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

}
