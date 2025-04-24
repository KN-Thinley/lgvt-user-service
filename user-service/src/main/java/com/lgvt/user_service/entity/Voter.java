package com.lgvt.user_service.entity;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lgvt.user_service.dao.CidDocument;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "voter")
@Data
public class Voter extends GeneralUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    @Size(min = 3, message = "Name must be at least 3 characters long")
    @NotBlank(message = "Name is required")
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Column(name = "password", nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;

    @Column(name = "cid", nullable = false, unique = true)
    @Digits(integer = 11, fraction = 0, message = "CID must be exactly 11 digits")
    @NotNull(message = "CID is required")
    private String cid;

    @Column(name = "phone", nullable = false)
    @Pattern(regexp = "^(77|17)\\d{6}$", message = "Phone number must have exactly 8 digits and start with 77 or 17")
    @NotNull(message = "Phone number is required")
    private String phone;

    @Embedded
    private CidDocument cid_document;

    @Column(name = "dzongkhag", nullable = false)
    @NotBlank(message = "Dzongkhag is required")
    private String dzongkhag;

    @Column(name = "gewog", nullable = false)
    @NotBlank(message = "Gewog is required")
    private String gewog;

    @Column(name = "village", nullable = false)
    @NotBlank(message = "Village is required")
    private String village;

    @Column(name = "dob", nullable = false)
    @NotNull(message = "Date of birth is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @Column(name = "gender", nullable = false)
    @NotNull(message = "Gender is required")
    private Gender gender;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "is_logged_in", nullable = false)
    private boolean logged_in = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.VOTER;

    @Column(name = "occupation", nullable = false)
    @NotBlank(message = "Occupation is required")
    private String occupation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Custom validation method to ensure age is 18 or older
    @AssertTrue(message = "Voter must be at least 18 years old")
    public boolean isAdult() {
        return dob != null && Period.between(dob, LocalDate.now()).getYears() >= 18;
    }
}
