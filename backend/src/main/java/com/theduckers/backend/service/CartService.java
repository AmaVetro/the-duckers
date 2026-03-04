package com.theduckers.backend.service;

import com.theduckers.backend.entity.ShoppingCart;
import com.theduckers.backend.entity.ShoppingCartItem;
import com.theduckers.backend.exception.BadRequestException;
import com.theduckers.backend.repository.ShoppingCartItemRepository;
import com.theduckers.backend.repository.ShoppingCartRepository;
import com.theduckers.backend.repository.mongo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//service/CartService:



@Service
public class CartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final ProductRepository productRepository;

    public CartService(
            ShoppingCartRepository shoppingCartRepository,
            ShoppingCartItemRepository shoppingCartItemRepository,
            ProductRepository productRepository
    ) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.productRepository = productRepository;
    }

    // =========================
    // Day 8 - Cart domain logic
    // =========================

    /**
     * Returns the ACTIVE shopping cart for the given user.
     *
     * Business rules:
     * - A user can have at most one ACTIVE cart.
     * - The cart is created lazily on first access.
     */
    @Transactional
    public ShoppingCart getOrCreateActiveCart(Long userId) {

        return shoppingCartRepository
                .findByUserIdAndStatus(userId, "ACTIVE")
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart(userId);
                    return shoppingCartRepository.save(newCart);
                });
    }





    /**
     * Adds a product to the user's active cart.
     *
     * Flow:
     * 1. Validate quantity > 0
     * 2. Get or create ACTIVE cart
     * 3. Fetch product from MongoDB
     * 4. Do NOT validate stock (intentionally deferred)
     * 5. If item exists -> increment quantity
     * 6. Else -> create new cart item with price snapshot
    */
    @Transactional
    public ShoppingCart addItem(Long userId, String productId, Integer quantity) {

        // 1️⃣ Validate quantity
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        // 2️⃣ Get or create ACTIVE cart
        ShoppingCart cart = getOrCreateActiveCart(userId);

        // 3️⃣ Fetch product from MongoDB
        var product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new BadRequestException("Product not found: " + productId)
                );

        // 4️⃣ Stock validation intentionally deferred to checkout

        // 5️⃣ Check if item already exists in cart
        shoppingCartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .ifPresentOrElse(
                        existingItem -> {
                            int newQuantity = existingItem.getQuantity() + quantity;
                            existingItem.updateQuantity(newQuantity);
                            shoppingCartItemRepository.save(existingItem);
                        },
                        () -> {
                            ShoppingCartItem newItem = new ShoppingCartItem(
                                    cart,
                                    product.getId(),
                                    product.getName(),
                                    product.getPrice(),
                                    quantity
                            );

                            // 🔥 CRITICAL: synchronize both sides of relationship
                            cart.getItems().add(newItem);

                            shoppingCartItemRepository.save(newItem);
                        }
                );

        // 6️⃣ Return cart (now correctly synchronized in memory)
        return cart;
    }





    /**
     * Updates the quantity of an existing cart item.
     *
     * Rules:
     * - Quantity must be >= 1
     * - The item must exist
     * - The item must belong to the user's ACTIVE cart
     */
    @Transactional
    public ShoppingCart updateItemQuantity(Long userId, Long itemId, Integer quantity) {

        // 1) Validate quantity
        if (quantity == null || quantity < 1) {
            throw new BadRequestException("Quantity must be at least 1");
        }

        // 2) Get user's ACTIVE cart
        ShoppingCart cart = getOrCreateActiveCart(userId);

        // 3) Fetch cart item by itemId
        ShoppingCartItem item = shoppingCartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new BadRequestException("Cart item not found: " + itemId)
                );

        // 4) Validate item belongs to user's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to user's cart");
        }

        // 5) Update quantity (subtotal recalculated in entity)
        item.updateQuantity(quantity);

        // 6) Persist changes
        shoppingCartItemRepository.save(item);

        // 7) Return updated cart (required by controller)
        return cart;
    }






    /**
     * Removes an item from the user's active cart.
     *
     * Rules:
     * - The item must exist
     * - The item must belong to the user's ACTIVE cart
     */
    @Transactional
    public ShoppingCart removeItem(Long userId, Long itemId) {

        // 1) Get user's ACTIVE cart
        ShoppingCart cart = getOrCreateActiveCart(userId);

        // 2) Fetch cart item by itemId
        ShoppingCartItem item = shoppingCartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new BadRequestException("Cart item not found: " + itemId)
                );

        // 3) Validate item belongs to user's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to user's cart");
        }

        // 4) Remove item
        shoppingCartItemRepository.delete(item);

        // 5) Return updated cart (required by controller)
        return cart;
    }



    

    @Transactional(readOnly = true)
    public ShoppingCart getActiveCart(Long userId) {
        return shoppingCartRepository
                .findByUserIdAndStatus(userId, "ACTIVE")
                .orElse(null);
    }






}
