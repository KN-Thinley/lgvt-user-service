package com.lgvt.user_service.dao;

import com.lgvt.user_service.entity.Invitation;
import com.lgvt.user_service.entity.InvitationStatus;
import com.lgvt.user_service.service.EmailService;
import com.lgvt.user_service.utils.InvitationEmailContext;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InvitationDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Invitation saveInvitation(Invitation invitation) {
        entityManager.persist(invitation);
        return invitation;
    }

    public Invitation findByEmail(String email) {
        String query = "SELECT i FROM Invitation i WHERE i.email = :email";
        TypedQuery<Invitation> typedQuery = entityManager.createQuery(query, Invitation.class);
        typedQuery.setParameter("email", email);

        return typedQuery.getResultStream().findFirst().orElse(null);
    }

    public String sendInvitationEmail(Invitation invitation) {
        // Prepare the email context
        InvitationEmailContext emailContext = new InvitationEmailContext();
        emailContext.init(invitation);
        emailContext.setToken(invitation.getCode());
        emailContext.setExpiresAt(invitation.getExpiresAt().toString());

        // Send the email
        try {
            emailService.sendInvitationMail(emailContext);
            return invitation.getCode();
        } catch (MessagingException e) {
            // Log the error for debugging purposes
            System.err.println("Failed to send invitation email: " + e.getMessage());
            e.printStackTrace();

            // Throw a custom exception to indicate email sending failure
            throw new RuntimeException("Failed to send invitation email. Please try again later.");
        }
    }

    public Invitation findById(Long id) {
        return entityManager.find(Invitation.class, id);
    }

    public List<Invitation> findAll() {
        String query = "SELECT i FROM Invitation i";
        return entityManager.createQuery(query, Invitation.class).getResultList();
    }

    public List<Invitation> findAllNonArchived() {
        String query = "SELECT i FROM Invitation i WHERE i.status != :archivedStatus";
        return entityManager.createQuery(query, Invitation.class)
                .setParameter("archivedStatus", InvitationStatus.ARCHIVED)
                .getResultList();
    }
}