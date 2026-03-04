package com.theduckers.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.theduckers.backend.exception.InvalidStateException;

// entity/Order

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private Long subtotal;

    // =========================
    // NEW: Separated discounts
    // =========================

    @Column(name = "duoc_discount", nullable = false)
    private Long duocDiscount;

    @Column(name = "points_discount", nullable = false)
    private Long pointsDiscount;

    @Column(nullable = false)
    private Long iva;

    @Column(nullable = false)
    private Long total;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    // Required by JPA for reflection-based instantiation
    protected Order() {
    }

    // =========================
    // Main constructor
    // =========================

    public Order(
            Long userId,
            Long subtotal,
            Long duocDiscount,
            Long pointsDiscount,
            Long iva,
            Long total
    ) {
        this.userId = userId;
        this.status = OrderStatus.PENDING;
        this.subtotal = subtotal;
        this.duocDiscount = duocDiscount;
        this.pointsDiscount = pointsDiscount;
        this.iva = iva;
        this.total = total;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // =========================
    // Getters
    // =========================

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Long getSubtotal() {
        return subtotal;
    }

    public Long getDuocDiscount() {
        return duocDiscount;
    }

    public Long getPointsDiscount() {
        return pointsDiscount;
    }

    // Public-facing total discount
    public Long getDiscount() {
        return (duocDiscount != null ? duocDiscount : 0L)
            + (pointsDiscount != null ? pointsDiscount : 0L);
    }

    public Long getIva() {
        return iva;
    }

    public Long getTotal() {
        return total;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    // =========================
    // Domain helpers
    // =========================

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public void markAsPaid() {

        if (this.status != OrderStatus.PENDING) {
            throw new InvalidStateException(
                    "Invalid state transition from " + this.status
            );
        }

        this.status = OrderStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCancelled() {

        if (this.status != OrderStatus.PENDING) {
            throw new InvalidStateException(
                    "Invalid state transition from " + this.status
            );
        }

        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
}