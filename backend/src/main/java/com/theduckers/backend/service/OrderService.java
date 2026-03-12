package com.theduckers.backend.service;

import com.theduckers.backend.entity.*;
import com.theduckers.backend.exception.BadRequestException;
import com.theduckers.backend.exception.InvalidStateException;
import com.theduckers.backend.repository.OrderRepository;
import com.theduckers.backend.repository.ShoppingCartRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.theduckers.backend.repository.UserPointsRepository;
import com.theduckers.backend.repository.PointRedemptionRepository;
import com.theduckers.backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;



//service/OrderService:


@Service
public class OrderService {

        private final ShoppingCartRepository shoppingCartRepository;
        private final OrderRepository orderRepository;
        private final StockService stockService;
        private final PointsService pointsService;
        private final UserPointsRepository userPointsRepository;
        private final PointRedemptionRepository pointRedemptionRepository;
        private final UserRepository userRepository;

        public OrderService(
                ShoppingCartRepository shoppingCartRepository,
                OrderRepository orderRepository,
                StockService stockService,
                PointsService pointsService,
                UserPointsRepository userPointsRepository,
                PointRedemptionRepository pointRedemptionRepository,
                UserRepository userRepository
        ) {
                this.shoppingCartRepository = shoppingCartRepository;
                this.orderRepository = orderRepository;
                this.stockService = stockService;
                this.pointsService = pointsService;
                this.userPointsRepository = userPointsRepository;
                this.pointRedemptionRepository = pointRedemptionRepository;
                this.userRepository = userRepository;
        }



        @Transactional
        public Order checkout(Long userId, boolean redeemPoints) {
                ShoppingCart cart = shoppingCartRepository
                        .findActiveCartWithItems(userId, "ACTIVE")
                        .orElseThrow(() ->
                                new BadRequestException("No active cart found")
                        );

                System.out.println("DEBUG CART ID: " + cart.getId());
                System.out.println("DEBUG CART ITEMS COUNT: " + cart.getItems().size());

                if (cart.getItems().isEmpty()) {
                        throw new BadRequestException("Cannot checkout an empty cart");
                }

                List<ReservedItem> reservedItems = new ArrayList<>();

                try {

                        // ==============================
                        // 1️⃣ Reserve stock
                        // ==============================

                        for (ShoppingCartItem cartItem : cart.getItems()) {

                        boolean reserved = stockService.reserveStock(
                                cartItem.getProductId(),
                                cartItem.getQuantity()
                        );

                        if (!reserved) {
                                throw new BadRequestException(
                                        "Insufficient stock for product: " + cartItem.getProductId()
                                );
                        }

                        reservedItems.add(
                                new ReservedItem(
                                        cartItem.getProductId(),
                                        cartItem.getQuantity()
                                )
                        );
                        }

                        // ==============================
                        // 2️⃣ Calculate subtotal
                        // ==============================

                        long subtotal = cart.getItems()
                                .stream()
                                .mapToLong(ShoppingCartItem::getSubtotal)
                                .sum();

                        // ==============================
                        // 3️⃣ Fetch user email
                        // ==============================

                        User user = userRepository.findById(userId)
                                .orElseThrow(() ->
                                        new BadRequestException("User not found")
                                );

                        String email = user.getEmail();

                        // ==============================
                        // 4️⃣ DUOC Discount (10%)
                        // ==============================

                        long duocDiscount = 0L;

                        if (isDuocUser(email)) {
                        duocDiscount = subtotal * 10 / 100;
                        }

                        // Defensive clamp
                        duocDiscount = Math.min(duocDiscount, subtotal);

                        // ==============================
                        // 5️⃣ Points Redemption (Cap 30%)
                        // ==============================

                        long pointsDiscount = 0L;

                        if (redeemPoints) {
                        RedemptionPreview preview = calculateRedemptionPreview(userId, subtotal);
                        pointsDiscount = preview.discountCLP();
                        }

                        // Ensure total discount never exceeds subtotal
                        if (duocDiscount + pointsDiscount > subtotal) {
                        pointsDiscount = subtotal - duocDiscount;
                        if (pointsDiscount < 0) {
                                pointsDiscount = 0;
                        }
                        }

                        // ==============================
                        // 6️⃣ Taxable Base (Base Imponible)
                        // ==============================

                        long baseAfterDiscounts = subtotal - duocDiscount - pointsDiscount;

                        if (baseAfterDiscounts < 0) {
                        baseAfterDiscounts = 0;
                        }

                        // ==============================
                        // 7️⃣ IVA 19%
                        // ==============================

                        long iva = baseAfterDiscounts * 19 / 100;

                        if (iva < 0) {
                        iva = 0;
                        }

                        // ==============================
                        // 8️⃣ Final Total
                        // ==============================

                        long total = baseAfterDiscounts + iva;

                        // ==============================
                        // 9️⃣ Create Order (Financially Complete)
                        // ==============================

                        Order order = new Order(
                                userId,
                                subtotal,
                                duocDiscount,
                                pointsDiscount,
                                iva,
                                total
                        );

                        // ==============================
                        // 🔟 Copy items into Order
                        // ==============================

                        for (ShoppingCartItem cartItem : cart.getItems()) {

                        OrderItem orderItem = new OrderItem(
                                order,
                                cartItem.getProductId(),
                                cartItem.getProductName(),
                                cartItem.getUnitPrice(),
                                cartItem.getQuantity(),
                                cartItem.getSubtotal()
                        );

                        order.addItem(orderItem);
                        }

                        orderRepository.save(order);

                        cart.markAsCheckedOut();
                        shoppingCartRepository.save(cart);

                        return order;

                } catch (Exception ex) {

                        // Restore reserved stock on failure
                        for (ReservedItem reservedItem : reservedItems) {
                        stockService.restoreStock(
                                reservedItem.productId(),
                                reservedItem.quantity()
                        );
                        }

                        throw ex;
                }
        }





        @Transactional
        public Order payOrder(Long userId, Long orderId) {

                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new BadRequestException("Order not found")
                        );

                if (!order.getUserId().equals(userId)) {
                        throw new BadRequestException("Order does not belong to user");
                }

                if (order.getStatus() != OrderStatus.PENDING) {
                        throw new InvalidStateException("Order is not in PENDING state");
                }

                if (order.getPointsDiscount() != null && order.getPointsDiscount() > 0) {

                        long pointsToDeduct = order.getPointsDiscount() * 100;

                        pointsService.deductPoints(userId, pointsToDeduct);

                        pointRedemptionRepository.save(
                                new PointRedemption(
                                        userId,
                                        orderId,
                                        pointsToDeduct,
                                        order.getPointsDiscount()
                                )
                        );
                }

                // ==============================
                // 2️⃣ Emit new points
                // ==============================

                long pointsToEmit = calculatePoints(order);

                pointsService.addPoints(userId, pointsToEmit);

                // ==============================
                // 3️⃣ Mark as paid
                // ==============================

                order.markAsPaid();

                orderRepository.save(order);

                return order;
        }





        @Transactional
        public Order cancelOrder(Long userId, Long orderId) {

                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new BadRequestException("Order not found")
                        );

                if (!order.getUserId().equals(userId)) {
                        throw new BadRequestException("Order does not belong to user");
                }

                // Restore stock ONLY if order is still PENDING
                if (order.getStatus() == OrderStatus.PENDING) {

                        for (OrderItem item : order.getItems()) {
                        stockService.restoreStock(
                                item.getProductId(),
                                item.getQuantity()
                        );
                        }
                }

                order.markAsCancelled();

                orderRepository.save(order);

                return order;
        }





        @Transactional(readOnly = true)
        public List<Order> getOrdersByUser(Long userId) {
                return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }





        @Transactional(readOnly = true)
        public Order getOrderById(Long userId, Long orderId) {

                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new BadRequestException("Order not found")
                        );

                if (!order.getUserId().equals(userId)) {
                throw new BadRequestException("Order does not belong to user");
                }

                return order;
        }



        private RedemptionPreview calculateRedemptionPreview(Long userId, long subtotal) {
                
                // 1️⃣ Fetch user points (read-only logic)
                UserPoints userPoints = userPointsRepository.findByUserId(userId)
                        .orElse(null);

                if (userPoints == null) {
                        return new RedemptionPreview(0L, 0L);
                }

                long balance = userPoints.getBalance();

                if (balance < 100) {
                        return new RedemptionPreview(0L, 0L);
                }

                // 2️⃣ Cap calculation (30%)
                long maxDiscountByCap = subtotal * 30 / 100;

                if (maxDiscountByCap <= 0) {
                        return new RedemptionPreview(0L, 0L);
                }

                // 3️⃣ Max discount allowed by available points
                long maxDiscountFromPoints = balance / 100;

                if (maxDiscountFromPoints <= 0) {
                        return new RedemptionPreview(0L, 0L);
                }

                // 4️⃣ Effective discount
                long discount = Math.min(maxDiscountByCap, maxDiscountFromPoints);

                // 🔒 Defensive mathematical clamp (Day 13 hardening)
                discount = Math.min(discount, subtotal);

                long pointsToDeduct = discount * 100;

                return new RedemptionPreview(discount, pointsToDeduct);
        }




        private long calculatePoints(Order order) {
                return order.getTotal();
        }


        private boolean isDuocUser(String email) {
                if (email == null) {
                        return false;
                }
                return email.toLowerCase().endsWith("@duocuc.cl");
        }




        private record ReservedItem(
                String productId,
                int quantity
        ) {}



        private record RedemptionPreview(
                long discountCLP,
                long pointsToDeduct
        ) {}
}