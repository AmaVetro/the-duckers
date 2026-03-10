// src/services/productService.js:

import { apiFetch } from "./apiClient";



/**
 * Fetch all products
 * Endpoint: GET /products
 */
export const getProducts = async (filters = {}) => {

    const params = new URLSearchParams();

    if (filters.text) {
        params.append("text", filters.text);
    }

    if (filters.category) {
        params.append("category", filters.category);
    }

    if (filters.minPrice) {
        params.append("minPrice", filters.minPrice);
    }

    if (filters.maxPrice) {
        params.append("maxPrice", filters.maxPrice);
    }

    const queryString = params.toString();

    const path = queryString
        ? `/products?${queryString}`
        : "/products";

    return await apiFetch(path);

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