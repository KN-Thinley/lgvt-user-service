package com.lgvt.user_service.service;

import com.lgvt.user_service.utils.AbstractEmailContext;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendMail(final AbstractEmailContext emai) throws MessagingException;

    void sendMFAMail(final AbstractEmailContext email) throws MessagingException;

    void sendForgotPasswordMail(final AbstractEmailContext email) throws MessagingException;
}
