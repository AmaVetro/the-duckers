package com.theduckers.backend.repository;

import com.theduckers.backend.entity.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


//Repository/ShoppingCartItemRepository:

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {

    /**
     * Returns a cart item by cart id and product id.
     *
     * Used to:
     * - Detect if a product is already present in the cart
     * - Increment quantity instead of creating duplicate items
     */
    Optional<ShoppingCartItem> findByCartIdAndProductId(Long cartId, String productId);

    /**
     * Returns all items belonging to a specific cart.
     *
     * Used to:
     * - Display the cart contents
     * - Calculate cart subtotal
     */
    List<ShoppingCartItem> findAllByCartId(Long cartId);
}
