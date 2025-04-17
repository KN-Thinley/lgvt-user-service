package com.lgvt.user_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lgvt.user_service.dao.VoterDAO;
import com.lgvt.user_service.entity.Voter;

@Service
public class CustomDetailsService implements UserDetailsService {
    @Autowired
    private VoterDAO voterDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // When different user comes in Add their logic here
        Voter voter = voterDAO.getVoterByEmail(username);
        if (voter == null) {
            System.out.println("Voter not found");
            throw new UsernameNotFoundException("Voter not found");
        }

        return new CustomUserDetails(voter);
    }

}
