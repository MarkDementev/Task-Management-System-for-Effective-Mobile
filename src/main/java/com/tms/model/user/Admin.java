package com.tms.model.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("admin")
@NoArgsConstructor
public class Admin extends User {
    public Admin(String email, String password) {
        super(email, password);
        super.setIsAdmin(true);
    }
}
