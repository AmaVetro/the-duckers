//src/services/cartService.js:


import { apiFetch } from "./apiClient";

export const getCart = async () => {
    return await apiFetch("/cart");
};

export const addItem = async (productId, quantity = 1) => {
    return await apiFetch("/cart/items", {
        method: "POST",
        body: JSON.stringify({
        productId,
        quantity
        })
    });
};

export const updateItem = async (itemId, quantity) => {
    return await apiFetch(`/cart/items/${itemId}`, {
        method: "PATCH",
        body: JSON.stringify({
        quantity
        })
    });
};

export const removeItem = async (itemId) => {
    return await apiFetch(`/cart/items/${itemId}`, {
        method: "DELETE"
    });
};