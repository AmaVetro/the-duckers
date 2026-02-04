package com.theduckers.backend.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    @JsonIgnore
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name_father", nullable = false, length = 100)
    private String lastNameFather;

    @Column(name = "last_name_mother", nullable = false, length = 100)
    private String lastNameMother;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(length = 255)
    private String address;

    @Column(name = "referral_code", nullable = false, length = 50, unique = true)
    private String referralCode;

    @Column(name = "referred_by_user_id")
    private Long referredByUserId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected User() { //Constructor PROTEGIDO (requerido por JPA) y vacío por ahora, no sé porqué
    }

    public User(
        String email,
        String passwordHash,
        String firstName,
        String lastNameFather,
        String lastNameMother,
        String referralCode,
        LocalDateTime createdAt
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastNameFather = lastNameFather;
        this.lastNameMother = lastNameMother;
        this.referralCode = referralCode;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }


    
    // Getters & setters

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Long getReferredByUserId() {
        return referredByUserId;
    }

    public void setReferredByUserId(Long referredByUserId) {
        this.referredByUserId = referredByUserId;
    }


}
