package com.lgvt.user_service.service;

import com.lgvt.user_service.dao.InvitationDAO;
import com.lgvt.user_service.entity.Invitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface InvitationService {
    Invitation saveInvitation(String email);
}