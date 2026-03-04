package com.theduckers.backend.dto.order;



//OrderItemResponse:



public class OrderItemResponse {

    private Long itemId;
    private String productId;
    private String productName;
    private Long unitPrice;
    private int quantity;
    private Long subtotal;

    public OrderItemResponse(
            Long itemId,
            String productId,
            String productName,
            Long unitPrice,
            int quantity,
            Long subtotal
    ) {
        this.itemId = itemId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getItemId() {
        return itemId;
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

    public int getQuantity() {
        return quantity;
    }

    public Long getSubtotal() {
        return subtotal;
    }
}
