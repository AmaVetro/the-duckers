package com.theduckers.backend.dto.cart;

import jakarta.validation.constraints.Min;


//dto/cart/UpdateItemRequest:

public class UpdateItemRequest {

    @Min(1)
    private int quantity;

    public int getQuantity() {
        return quantity;
    }
}
