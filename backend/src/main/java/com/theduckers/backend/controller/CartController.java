package com.theduckers.backend.controller;

import com.theduckers.backend.dto.cart.AddItemRequest;
import com.theduckers.backend.dto.cart.CartItemResponse;
import com.theduckers.backend.dto.cart.CartResponse;
import com.theduckers.backend.dto.cart.UpdateItemRequest;
import com.theduckers.backend.entity.ShoppingCart;
import com.theduckers.backend.entity.ShoppingCartItem;
import com.theduckers.backend.security.UserDetailsImpl;
import com.theduckers.backend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.theduckers.backend.config.OpenApiConfig.SECURITY_SCHEME_NAME;

//controller/CartController:

@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/cart")
public class CartController {

        private final CartService cartService;

        public CartController(CartService cartService) {
                this.cartService = cartService;
        }

        // =========================
        // GET ACTIVE CART
        // =========================
        @Operation(
                summary = "Get active shopping cart",
                description = "Returns the user's active shopping cart. If no ACTIVE cart exists, one is created lazily."
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Cart retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CartResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content
                )
        })
        @GetMapping
        public CartResponse getCart(
                @AuthenticationPrincipal UserDetailsImpl userDetails
        ) {
                ShoppingCart cart = cartService.getOrCreateActiveCart(userDetails.getId());
                return mapToResponse(cart);
        }

        // =========================
        // ADD ITEM
        // =========================
        @Operation(
                summary = "Add product to cart",
                description = "Adds a product to the user's active cart. Stock validation is intentionally deferred to checkout."
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Item added successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CartResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request body or product not found",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content
                )
        })
        @PostMapping("/items")
        public CartResponse addItem(
                @AuthenticationPrincipal UserDetailsImpl userDetails,
                @Valid @RequestBody AddItemRequest request
        ) {
                ShoppingCart cart = cartService.addItem(
                        userDetails.getId(),
                        request.getProductId(),
                        request.getQuantity()
                );

                return mapToResponse(cart);
        }

        // =========================
        // UPDATE ITEM QUANTITY
        // =========================
        @Operation(
                summary = "Update cart item quantity",
                description = "Updates the quantity of a specific cart item. Quantity must be at least 1."
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Cart updated successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CartResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid quantity or item not found",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content
                )
        })
        @PatchMapping("/items/{itemId}")
        public CartResponse updateItemQuantity(
                @AuthenticationPrincipal UserDetailsImpl userDetails,
                @PathVariable Long itemId,
                @Valid @RequestBody UpdateItemRequest request
        ) {
                ShoppingCart cart = cartService.updateItemQuantity(
                        userDetails.getId(),
                        itemId,
                        request.getQuantity()
                );

                return mapToResponse(cart);
        }

        // =========================
        // REMOVE ITEM
        // =========================
        @Operation(
                summary = "Remove item from cart",
                description = "Removes a specific item from the user's active shopping cart."
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Item removed successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CartResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Cart item not found",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content
                )
        })
        @DeleteMapping("/items/{itemId}")
        public CartResponse removeItem(
                @AuthenticationPrincipal UserDetailsImpl userDetails,
                @PathVariable Long itemId
        ) {
                ShoppingCart cart = cartService.removeItem(
                        userDetails.getId(),
                        itemId
                );

                return mapToResponse(cart);
        }

        // =========================
        // PRIVATE MAPPER
        // =========================
        private CartResponse mapToResponse(ShoppingCart cart) {

                List<CartItemResponse> items = cart.getItems()
                        .stream()
                        .map(this::mapToItemResponse)
                        .toList();

                long subtotal = items.stream()
                        .mapToLong(CartItemResponse::getSubtotal)
                        .sum();

                return new CartResponse(
                        cart.getId(),
                        cart.getStatus(),
                        items,
                        subtotal
                );
        }

        private CartItemResponse mapToItemResponse(ShoppingCartItem item) {
                return new CartItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                );
        }
}