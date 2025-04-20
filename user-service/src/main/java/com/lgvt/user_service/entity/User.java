package com.lgvt.user_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = true)
    @Size(min = 3, message = "Name must be at least 3 characters long")
    @NotBlank(message = "Name is required")
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Column(name = "phone", nullable = false)
    @Pattern(regexp = "^(77|17)\\d{6}$", message = "Phone number must have exactly 8 digits and start with 77 or 17")
    @NotNull(message = "Phone number is required")
    private String phone;

    @Column(name = "dzongkhag", nullable = true)
    @NotBlank(message = "Dzongkhag is required")
    private String dzongkhag;

    @Column(name = "gewog", nullable = true)
    @NotBlank(message = "Gewog is required")
    private String gewog;

    @Column(name = "password", nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING; // Default value set to PENDING

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Automatically set to the current time

    // Setter for role to update status if role is SUPER_ADMIN
    public void setRole(Role role) {
        this.role = role;
        if (role == Role.SUPER_ADMIN) {
            this.status = InvitationStatus.ACCEPTED;
        }
    }
}