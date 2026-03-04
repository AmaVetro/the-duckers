package com.theduckers.backend.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;



//entity/ShoppingCartItem:


@Entity
@Table(name = "shopping_cart_items")
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;

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

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    protected ShoppingCartItem() {
        // Required by JPA
    }

    public ShoppingCartItem(
            ShoppingCart cart,
            String productId,
            String productName,
            Long unitPrice,
            Integer quantity
    ) {
        this.cart = cart;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice * quantity;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }


    public void updateQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
        this.subtotal = this.unitPrice * newQuantity;
        this.updatedAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public ShoppingCart getCart() {
        return cart;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }



}
