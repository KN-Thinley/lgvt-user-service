package com.lgvt.user_service.entity;

import java.math.BigInteger;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lgvt.user_service.dao.CidDocument;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "voter")
@Data
public class Voter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "cid", nullable = false)
    private BigInteger cid;

    @Column(name = "phone", nullable = false)
    private long phone;

    @Embedded
    private CidDocument cid_document;

    @Column(name = "dzongkhag", nullable = false)
    private String dzongkhag;

    @Column(name = "gewog", nullable = false)
    private String gewog;

    @Column(name = "village", nullable = false)
    private String village;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "gender", nullable = false)
    private Gender gender;

    public enum Gender {
        MALE, FEMALE
    }
}
