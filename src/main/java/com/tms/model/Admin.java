package com.tms.model;

import jakarta.persistence.Entity;

@Entity
public class Admin extends User {
    public Admin(String email, String password) {
        super(email, password);
        super.setIsAdmin(true);
    }
}