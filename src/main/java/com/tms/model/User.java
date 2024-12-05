package com.tms.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "all_users_and_admin")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Boolean isAdmin;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.isAdmin = false;
    }
}
