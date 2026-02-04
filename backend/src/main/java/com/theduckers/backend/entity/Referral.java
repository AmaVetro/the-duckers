package com.theduckers.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "referrals")
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "referrer_user_id", nullable = false)
    private Long referrerUserId;

    @Column(name = "referred_user_id", nullable = false, unique = true)
    private Long referredUserId;

    @Column(name = "bonus_points", nullable = false)
    private Long bonusPoints;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Referral() {
        // requerido por JPA
    }

    public Referral(
            Long referrerUserId,
            Long referredUserId,
            Long bonusPoints
    ) {
        this.referrerUserId = referrerUserId;
        this.referredUserId = referredUserId;
        this.bonusPoints = bonusPoints;
        this.createdAt = LocalDateTime.now();
    }

    public Long getReferrerUserId() {
        return referrerUserId;
    }

    public Long getReferredUserId() {
        return referredUserId;
    }

    public Long getBonusPoints() {
        return bonusPoints;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
