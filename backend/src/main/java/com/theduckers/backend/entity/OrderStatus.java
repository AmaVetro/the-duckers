package com.theduckers.backend.entity;


/**
 * Represents the lifecycle states of an Order.
 *
 * PENDING   -> Order created but not yet paid
 * PAID      -> Order successfully paid
 * CANCELLED -> Order cancelled before payment
 */


//entity/OrderStatus:

public enum OrderStatus {
    PENDING,
    PAID,
    CANCELLED
}