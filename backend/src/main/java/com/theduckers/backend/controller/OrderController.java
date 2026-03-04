package com.theduckers.backend.controller;

import com.theduckers.backend.dto.order.CheckoutRequest;
import com.theduckers.backend.dto.order.OrderItemResponse;
import com.theduckers.backend.dto.order.OrderResponse;
import com.theduckers.backend.entity.Order;
import com.theduckers.backend.entity.OrderItem;
import com.theduckers.backend.security.UserDetailsImpl;
import com.theduckers.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.theduckers.backend.config.OpenApiConfig.SECURITY_SCHEME_NAME;



//controller/OrderController:

@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RestController
@RequestMapping
public class OrderController {

        private final OrderService orderService;

        public OrderController(OrderService orderService) {
                this.orderService = orderService;
        }

        // =========================
        // CHECKOUT
        // =========================
        @Operation(
        summary = "Checkout active cart",
        description = """
                Creates a PENDING order from the user's ACTIVE cart.

                Financial calculation model:

                1) Subtotal:
                Sum of all cart item subtotals.

                2) DUOC Discount (10%):
                Applied if user email ends with @duocuc.cl.

                3) Loyalty Points Redemption (optional):
                - 100 points = 1 CLP
                - Capped at 30% of subtotal
                - Applied BEFORE VAT
                - Points are deducted ONLY after successful payment

                4) Taxable Base (Base Imponible):
                Base = subtotal - duocDiscount - pointsDiscount

                5) VAT (IVA 19%):
                IVA = Base * 0.19

                6) Final Total:
                Total = Base + IVA

                Important:
                - All monetary values are expressed in CLP.
                - Financial fields are immutable once the order is created.
                - Database-level CHECK constraints enforce formula integrity.
                - Stock reservation is atomic and rolled back on failure.

                Redemption activation:
                - Send { "redeemPoints": true } to activate redemption.
                - Omit body or send false to skip redemption.
                """
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Order created successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = OrderResponse.class)
                        )
                ),
                @ApiResponse(responseCode = "400", description = "Empty cart or insufficient stock"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @PostMapping("/checkout")
        public OrderResponse checkout(
                @AuthenticationPrincipal UserDetailsImpl userDetails,
                @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Optional checkout configuration",
                        required = false,
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CheckoutRequest.class),
                                examples = {
                                        @ExampleObject(
                                                name = "Redeem points",
                                                summary = "Activate loyalty redemption",
                                                value = """
                                                        {
                                                        "redeemPoints": true
                                                        }
                                                        """
                                        ),
                                        @ExampleObject(
                                                name = "No redemption",
                                                summary = "Standard checkout without loyalty usage",
                                                value = """
                                                        {
                                                        "redeemPoints": false
                                                        }
                                                        """
                                        )
                                }
                        )
                )
                @RequestBody(required = false) CheckoutRequest request
        ) {
                boolean redeemPoints = request != null && request.shouldRedeemPoints();
                Order order = orderService.checkout(
                        userDetails.getId(),
                        redeemPoints
                );
                return mapToResponse(order);
        }




        // =========================
        // PAY
        // =========================
        @Operation(
                summary = "Pay order (simulate payment)",
                description = """
                        Confirms payment for a PENDING order.
                        - Deducts redeemed points (if any).
                        - Emits new loyalty points based on final total.
                        - Marks order as PAID.
                        """
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Order paid successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = OrderResponse.class)
                        )
                ),
                @ApiResponse(responseCode = "400", description = "Invalid state or order not found"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @PostMapping("/orders/{id}/pay")
        public OrderResponse payOrder(
                @AuthenticationPrincipal UserDetailsImpl userDetails,
                @PathVariable("id") Long orderId
        ) {

                Order order = orderService.payOrder(userDetails.getId(), orderId);

                return mapToResponse(order);
        }

        // =========================
        // CANCEL
        // =========================
        @Operation(
                summary = "Cancel order",
                description = "Cancels a PENDING order and restores reserved stock."
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Order cancelled successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = OrderResponse.class)
                        )
                ),
                @ApiResponse(responseCode = "400", description = "Invalid state or order not found"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @PostMapping("/orders/{id}/cancel")
        public OrderResponse cancelOrder(
                @AuthenticationPrincipal UserDetailsImpl userDetails,
                @PathVariable("id") Long orderId
        ) {

                Order order = orderService.cancelOrder(userDetails.getId(), orderId);

                return mapToResponse(order);
        }

        // =========================
        // GET ORDERS
        // =========================
        @Operation(
                summary = "Get user order history",
                description = "Returns all orders for the authenticated user, ordered by creation date descending."
        )
        @GetMapping("/orders")
        public List<OrderResponse> getOrders(
                @AuthenticationPrincipal UserDetailsImpl userDetails
        ) {

                return orderService.getOrdersByUser(userDetails.getId())
                        .stream()
                        .map(this::mapToResponse)
                        .toList();
        }

        // =========================
        // GET ORDER BY ID
        // =========================
        @Operation(
                summary = "Get order detail",
                description = "Returns detailed information for a specific order belonging to the authenticated user."
        )
        @GetMapping("/orders/{id}")
        public OrderResponse getOrderById(
                @AuthenticationPrincipal UserDetailsImpl userDetails,
                @PathVariable("id") Long orderId
        ) {

                Order order = orderService.getOrderById(userDetails.getId(), orderId);

                return mapToResponse(order);
        }

        // =========================
        // Private Mapper
        // =========================
        private OrderResponse mapToResponse(Order order) {

                List<OrderItemResponse> items = order.getItems()
                        .stream()
                        .map(this::mapToItemResponse)
                        .toList();

                return new OrderResponse(
                        order.getId(),
                        order.getStatus(),
                        order.getSubtotal(),
                        order.getDuocDiscount(),
                        order.getPointsDiscount(),
                        order.getDiscount(), // calculated inside entity
                        order.getIva(),
                        order.getTotal(),
                        order.getCreatedAt(),
                        items
                );
        }

        private OrderItemResponse mapToItemResponse(OrderItem item) {
                return new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                );
        }
}