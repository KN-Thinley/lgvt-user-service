package com.lgvt.user_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GeneralUser {

    public abstract String getPassword();

    public abstract String getEmail();

    public abstract Role getRole();

    public abstract int getId();

    public abstract Object getName();
}
