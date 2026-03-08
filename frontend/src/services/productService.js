// src/services/productService.js

import { apiFetch } from "./apiClient";



/**
 * Fetch all products
 * Endpoint: GET /products
 */
export const getProducts = async () => {

    return await apiFetch("/products");

};



/**
 * Fetch product by id
 * Endpoint: GET /products/{id}
 */
export const getProductById = async (id) => {

    try {

        return await apiFetch(`/products/${id}`);

    } catch (error) {

        if (error.message.includes("404")) {
            return null;
        }

        throw error;

    }

};



/**
 * Calculate cart total (pure frontend utility)
 */
export const calculateTotal = (items) => {

    return items.reduce(
        (accumulator, item) => accumulator + item.product.price * item.quantity,
        0
    );

};