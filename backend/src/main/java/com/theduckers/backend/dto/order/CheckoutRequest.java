package com.theduckers.backend.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;


//dto/order/CheckoutRequest:


@Schema(description = """
        Optional checkout configuration.

        If redeemPoints = true:
        - Loyalty points redemption is calculated (100 points = 1 CLP).
        - Redemption is capped at 30% of subtotal.
        - Points are deducted only after successful payment.

        If redeemPoints is false or omitted:
        - No points are used.
        - User earns full loyalty emission after payment.
        """)
public class CheckoutRequest {

    @Schema(
            description = "Activates loyalty points redemption if true. If omitted or false, no points are used.",
            example = "true"
    )
    private Boolean redeemPoints;

    public CheckoutRequest() {
    }

    public Boolean getRedeemPoints() {
        return redeemPoints;
    }

    public void setRedeemPoints(Boolean redeemPoints) {
        this.redeemPoints = redeemPoints;
    }

    /**
     * Safe helper:
     * Returns true only if explicitly true.
     */
    public boolean shouldRedeemPoints() {
        return Boolean.TRUE.equals(redeemPoints);
    }
}