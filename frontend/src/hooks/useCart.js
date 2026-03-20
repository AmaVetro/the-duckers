//frontend/src/hooks/useCart.js:


import { useEffect, useState } from "react";
import { getCart, addItem, removeItem, updateItem } from "../services/cartService";

export const useCart = () => {

    const [cartItems, setCartItems] = useState([]);
    const [loadingCart, setLoadingCart] = useState(true);



    const loadCart = async (showLoading = true) => {

        if (showLoading) {
            setLoadingCart(true);
        }

        try {
            const response = await getCart();
            const items = response?.items ?? [];
            setCartItems(items);
        } catch (error) {

            const errorMessage = String(error);

            if (errorMessage.includes("401")) {
                setCartItems([]);
            } else {
                console.error("Error loading cart", error);
                setCartItems([]);
            }

        } finally {
            if (showLoading) {
                setLoadingCart(false);
            }
        }
    };


    useEffect(() => {

        loadCart();

        const handleCartUpdate = () => {
            loadCart();
        };

        window.addEventListener("cart-updated", handleCartUpdate);

        return () => {
            window.removeEventListener("cart-updated", handleCartUpdate);
        };

    }, []);




    const handlerAddProductCart = async (product) => {

        try {

        await addItem(product.id, 1);

        await loadCart(false);

        } catch (error) {

        console.error("Error adding product to cart", error);

        }

    };




    const handlerDeleteProductCart = async (itemId) => {
        try {

            await removeItem(itemId);

            await loadCart(false);

        } catch (error) {

            console.error("Error removing item from cart", error);
        }
    };




    // ==============================
    // Update cart item quantity
    // ==============================

    const updateQuantity = async (itemId, quantity) => {
        try {

            await updateItem(itemId, quantity);

            await loadCart(false);

        } catch (error) {

            console.error("Error updating item quantity", error);
        }
    };



    return {
        cartItems,
        loadingCart,
        handlerAddProductCart,
        handlerDeleteProductCart,
        updateQuantity,
        loadCart
    };

};