package com.theduckers.backend.dto.cart;

import java.util.List;


//dto/cart/CartResponse:

public class CartResponse {

    private Long cartId;
    private String status;
    private List<CartItemResponse> items;
    private Long subtotal;

    public CartResponse(
            Long cartId,
            String status,
            List<CartItemResponse> items,
            Long subtotal
    ) {
        this.cartId = cartId;
        this.status = status;
        this.items = items;
        this.subtotal = subtotal;
    }

    public Long getCartId() {
        return cartId;
    }

    public String getStatus() {
        return status;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public Long getSubtotal() {
        return subtotal;
    }
}
