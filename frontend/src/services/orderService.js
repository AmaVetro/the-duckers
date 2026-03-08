// src/services/orderService.js

import { apiFetch } from "./apiClient";


/**
 * Create order from current cart
 * Endpoint: POST /checkout
 *
 * Optional body:
 * {
 *   redeemPoints: true
 * }s
 */
export const checkout = async (redeemPoints = false) => {

    return await apiFetch("/checkout", {
        method: "POST",
        body: JSON.stringify({
            redeemPoints
        })
    });

};



/**
 * Pay an order
 * Endpoint: POST /orders/{id}/pay
 */
export const payOrder = async (orderId) => {

    return await apiFetch(`/orders/${orderId}/pay`, {
        method: "POST"
    });

};



/**
 * Cancel an order
 * Endpoint: POST /orders/{id}/cancel
 */
export const cancelOrder = async (orderId) => {

    return await apiFetch(`/orders/${orderId}/cancel`, {
        method: "POST"
    });

};



/**
 * Fetch all user orders
 * Endpoint: GET /orders
 */
export const getOrders = async () => {

    return await apiFetch("/orders");

};



/**
 * Fetch single order
 * Endpoint: GET /orders/{id}
 */
export const getOrderById = async (orderId) => {

    return await apiFetch(`/orders/${orderId}`);

};