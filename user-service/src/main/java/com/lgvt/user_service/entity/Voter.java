package com.lgvt.user_service.entity;

import java.math.BigInteger;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "voter")
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

    @Column(name = "cid_document_url", nullable = false)
    private String cid_document_url;

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

    public Voter() {
    }

    public Voter(int id, String name, String email, String password, BigInteger cid, long phone,
            String cid_document_url, String dzongkhag, String gewog, String village, LocalDate dob, Gender gender) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.cid = cid;
        this.phone = phone;
        this.cid_document_url = cid_document_url;
        this.dzongkhag = dzongkhag;
        this.gewog = gewog;
        this.village = village;
        this.dob = dob;
        this.gender = gender;
    }

    public enum Gender {
        MALE, FEMALE
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigInteger getCid() {
        return cid;
    }

    public void setCid(BigInteger cid) {
        this.cid = cid;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getCid_document_url() {
        return cid_document_url;
    }

    public void setCid_document_url(String cid_document_url) {
        this.cid_document_url = cid_document_url;
    }

    public String getDzongkhag() {
        return dzongkhag;
    }

    public void setDzongkhag(String dzongkhag) {
        this.dzongkhag = dzongkhag;
    }

    public String getGewog() {
        return gewog;
    }

    public void setGewog(String gewog) {
        this.gewog = gewog;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
