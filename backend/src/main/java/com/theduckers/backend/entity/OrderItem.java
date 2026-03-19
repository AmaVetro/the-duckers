package com.theduckers.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;


//entity/OrderItem:


@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false, length = 100)
    private String productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "unit_price", nullable = false)
    private Long unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long subtotal;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected OrderItem() {
    }

    public OrderItem(
            Order order,
            String productId,
            String productName,
            Long unitPrice,
            Integer quantity,
            Long subtotal
    ) {
        this.order = order;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.createdAt = LocalDateTime.now();
    }

    // =========================
    // Getters
    // =========================

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getSubtotal() {
        return subtotal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
