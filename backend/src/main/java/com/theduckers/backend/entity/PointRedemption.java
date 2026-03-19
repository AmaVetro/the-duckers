package com.theduckers.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;



//entity/PointRedemption:

@Entity
@Table(name = "point_redemptions")
public class PointRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "points_used", nullable = false)
    private Long pointsUsed;

    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected PointRedemption() {
    }

    public PointRedemption(
            Long userId,
            Long orderId,
            Long pointsUsed,
            Long discountAmount
    ) {
        this.userId = userId;
        this.orderId = orderId;
        this.pointsUsed = pointsUsed;
        this.discountAmount = discountAmount;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getPointsUsed() {
        return pointsUsed;
    }

    public Long getDiscountAmount() {
        return discountAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}