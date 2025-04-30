package com.lgvt.user_service.service;

import com.lgvt.user_service.dao.InvitationDAO;
import com.lgvt.user_service.entity.Invitation;
import com.lgvt.user_service.entity.User;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface InvitationService {
    Invitation saveInvitation(String email);

    Invitation resendInvitation(String email);

    Long verifyInvitation(String email, String code);

    void registerAdmin(Long invitationId, User user);

    List<Map<String, Object>> getInvitationAndUserDetails();

    void deleteUserOrInvitation(String email);
}