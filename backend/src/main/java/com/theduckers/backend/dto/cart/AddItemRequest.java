package com.theduckers.backend.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


//dto/cart/AddItemRequest:


public class AddItemRequest {

    @NotBlank
    private String productId;

    @Min(1)
    private int quantity;

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
