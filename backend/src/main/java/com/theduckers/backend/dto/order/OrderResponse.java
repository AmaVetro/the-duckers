package com.theduckers.backend.dto.order;

import com.theduckers.backend.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

// dto/order/OrderResponse

public class OrderResponse {

    private Long orderId;
    private OrderStatus status;

    private Long subtotal;

    // Transparent financial breakdown
    private Long duocDiscount;
    private Long pointsDiscount;
    private Long discount; // total discount (duoc + points)

    private Long iva;
    private Long total;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;

    public OrderResponse(
            Long orderId,
            OrderStatus status,
            Long subtotal,
            Long duocDiscount,
            Long pointsDiscount,
            Long discount,
            Long iva,
            Long total,
            LocalDateTime createdAt,
            List<OrderItemResponse> items
    ) {
        this.orderId = orderId;
        this.status = status;
        this.subtotal = subtotal;
        this.duocDiscount = duocDiscount;
        this.pointsDiscount = pointsDiscount;
        this.discount = discount;
        this.iva = iva;
        this.total = total;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
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

    public Long getDiscount() {
        return discount;
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

    public List<OrderItemResponse> getItems() {
        return items;
    }
}