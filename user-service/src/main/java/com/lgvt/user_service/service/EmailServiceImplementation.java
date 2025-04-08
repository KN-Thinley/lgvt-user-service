package com.lgvt.user_service.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.lgvt.user_service.utils.AbstractEmailContext;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImplementation implements EmailService {
  @Autowired
  private JavaMailSender emailSender;

  @Override
  public void sendMail(final AbstractEmailContext email) throws MessagingException {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
        StandardCharsets.UTF_8.name());

    // Create the email content manually
    String emailContent = "<html><body>"
        + "<h1>Welcome, " + email.getContext().get("name") + " !</h1>"
        + "<p>Thank you for registering. Please click the link below to verify your email:</p>"
        + "<p>Your verification code is: <b>" + email.getOtp() + "</b></p>"
        + "</body></html>";

    // Debug logs
    System.out.println("Sending email to: " + email.getTo());
    System.out.println("Email subject: " + email.getSubject());
    System.out.println("Email content: " + emailContent);

    messageHelper.setTo(email.getTo());
    messageHelper.setFrom(email.getFrom());
    messageHelper.setSubject(email.getSubject());
    messageHelper.setText(emailContent, true);

    emailSender.send(message);
    System.out.println("Email sent successfully to: " + email.getTo());
  }

  @Override
  public void sendMFAMail(final AbstractEmailContext email) throws MessagingException {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
        StandardCharsets.UTF_8.name());

    // Create the email content manually
    String emailContent = "<html><body>"
        + "<h1>Multi-Factor Authentication (MFA)</h1>"
        + "<p>Dear " + email.getContext().get("name") + ",</p>"
        + "<p>We received a request to log in to your account. Please use the verification code below to complete the login process:</p>"
        + "<p style='font-size: 18px; font-weight: bold;'>Your verification code: <b>" + email.getOtp() + "</b></p>"
        + "<p>If you did not request this, please ignore this email or contact support immediately.</p>"
        + "<p>Thank you,</p>"
        + "<p>The LGVT User Service Team</p>"
        + "</body></html>";

    // Debug logs
    System.out.println("Sending email to: " + email.getTo());
    System.out.println("Email subject: " + email.getSubject());
    System.out.println("Email content: " + emailContent);

    messageHelper.setTo(email.getTo());
    messageHelper.setFrom(email.getFrom());
    messageHelper.setSubject(email.getSubject());
    messageHelper.setText(emailContent, true);

    emailSender.send(message);
    System.out.println("Email sent successfully to: " + email.getTo());
  }

  @Override
  public void sendForgotPasswordMail(final AbstractEmailContext email) throws MessagingException {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
        StandardCharsets.UTF_8.name());

    // Create the email content manually
    String emailContent = "<html><body>"
        + "<h1>Password Reset Request</h1>"
        + "<p>Dear " + email.getContext().get("name") + ",</p>"
        + "<p>We received a request to reset the password for your account. Please use the verification code below to complete the password reset process:</p>"
        + "<p style='font-size: 18px; font-weight: bold;'>Your verification code: <b>" + email.getOtp() + "</b></p>"
        + "<p>If you did not request this, please ignore this email or contact support immediately.</p>"
        + "<p>Thank you,</p>"
        + "<p>The LGVT User Service Team</p>"
        + "</body></html>";

    // Debug logs
    System.out.println("Sending email to: " + email.getTo());
    System.out.println("Email subject: " + email.getSubject());
    System.out.println("Email content: " + emailContent);

    messageHelper.setTo(email.getTo());
    messageHelper.setFrom(email.getFrom());
    messageHelper.setSubject(email.getSubject());
    messageHelper.setText(emailContent, true);

    emailSender.send(message);
    System.out.println("Email sent successfully to: " + email.getTo());
  }

}
