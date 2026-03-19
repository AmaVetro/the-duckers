package com.theduckers.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theduckers.backend.entity.Order;
import com.theduckers.backend.entity.OrderStatus;
import com.theduckers.backend.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import com.theduckers.backend.entity.UserPoints;
import com.theduckers.backend.repository.UserPointsRepository;

import com.theduckers.backend.repository.PointRedemptionRepository;



//test/integration/OrderStateIntegrationTest:

public class OrderStateIntegrationTest extends AbstractIntegrationTest {

        @Autowired
        private PointRedemptionRepository pointRedemptionRepository;

        @Autowired
        private com.theduckers.backend.repository.UserRepository userRepository;

        @Autowired
        private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private UserPointsRepository userPointsRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private final String TEST_PASSWORD = "Password123";

        @org.junit.jupiter.api.BeforeEach
        void cleanMongo() {
                mongoTemplate.dropCollection("products");
        }

        @Test
        void pay_order_successfully() throws Exception {

                String email = "pay_" + System.currentTimeMillis() + "@email.com";

                var product = new com.theduckers.backend.entity.mongo.ProductDocument();
                product.setId("keyboard-001");
                product.setName("Mechanical Keyboard");
                product.setPrice(50000L);
                product.setStock(2);
                mongoTemplate.save(product);

                String registerJson = """
                        {
                        "firstName":"Test",
                        "lastNameFather":"UserFather",
                        "lastNameMother":"UserMother",
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson))
                        .andExpect(status().isCreated());

                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                var loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String token = objectMapper.readTree(
                        loginResult.getResponse().getContentAsString()
                ).get("token").asText();

                String addItemJson = """
                        {
                        "productId":"keyboard-001",
                        "quantity":2
                        }
                        """;

                mockMvc.perform(post("/cart/items")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addItemJson))
                        .andExpect(status().isOk());

                var checkoutResult = mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PENDING"))
                        .andReturn();

                Long orderId = objectMapper.readTree(
                        checkoutResult.getResponse().getContentAsString()
                ).get("orderId").asLong();

                mockMvc.perform(post("/orders/" + orderId + "/pay")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PAID"));

                Order updated = orderRepository.findById(orderId).orElseThrow();

                assertEquals(OrderStatus.PAID, updated.getStatus());
                assertNotNull(updated.getPaidAt());
        }

        @Test
        void cancel_order_successfully() throws Exception {

                String email = "cancel_" + System.currentTimeMillis() + "@email.com";

                var product = new com.theduckers.backend.entity.mongo.ProductDocument();
                product.setId("keyboard-002");
                product.setName("Gaming Keyboard");
                product.setPrice(40000L);
                product.setStock(2);
                mongoTemplate.save(product);

                String registerJson = """
                        {
                        "firstName":"Test",
                        "lastNameFather":"UserFather",
                        "lastNameMother":"UserMother",
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson))
                        .andExpect(status().isCreated());

                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                var loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String token = objectMapper.readTree(
                        loginResult.getResponse().getContentAsString()
                ).get("token").asText();

                String addItemJson = """
                        {
                        "productId":"keyboard-002",
                        "quantity":1
                        }
                        """;

                mockMvc.perform(post("/cart/items")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addItemJson))
                        .andExpect(status().isOk());

                var checkoutResult = mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PENDING"))
                        .andReturn();

                Long orderId = objectMapper.readTree(
                        checkoutResult.getResponse().getContentAsString()
                ).get("orderId").asLong();

                mockMvc.perform(post("/orders/" + orderId + "/cancel")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("CANCELLED"));

                Order updated = orderRepository.findById(orderId).orElseThrow();

                assertEquals(OrderStatus.CANCELLED, updated.getStatus());
        }

        @Test
        void pay_order_twice_should_fail_with_invalid_state() throws Exception {

                String email = "invalid_" + System.currentTimeMillis() + "@email.com";

                var product = new com.theduckers.backend.entity.mongo.ProductDocument();
                product.setId("keyboard-003");
                product.setName("RGB Keyboard");
                product.setPrice(60000L);
                product.setStock(2);
                mongoTemplate.save(product);

                String registerJson = """
                        {
                        "firstName":"Test",
                        "lastNameFather":"UserFather",
                        "lastNameMother":"UserMother",
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson))
                        .andExpect(status().isCreated());

                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                var loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String token = objectMapper.readTree(
                        loginResult.getResponse().getContentAsString()
                ).get("token").asText();

                String addItemJson = """
                        {
                        "productId":"keyboard-003",
                        "quantity":1
                        }
                        """;

                mockMvc.perform(post("/cart/items")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addItemJson))
                        .andExpect(status().isOk());

                var checkoutResult = mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();

                Long orderId = objectMapper.readTree(
                        checkoutResult.getResponse().getContentAsString()
                ).get("orderId").asLong();

                mockMvc.perform(post("/orders/" + orderId + "/pay")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PAID"));

                mockMvc.perform(post("/orders/" + orderId + "/pay")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isBadRequest());

                Order updated = orderRepository.findById(orderId).orElseThrow();

                assertEquals(OrderStatus.PAID, updated.getStatus());
        }





        @Test
        void payment_should_emit_points_and_update_level() throws Exception {

                String email = "points_" + System.currentTimeMillis() + "@email.com";

                // Seed product worth 100000 CLP
                var product = new com.theduckers.backend.entity.mongo.ProductDocument();
                product.setId("keyboard-100k");
                product.setName("Premium Keyboard");
                product.setPrice(100000L);
                product.setStock(1);
                mongoTemplate.save(product);

                // Register
                String registerJson = """
                        {
                        "firstName":"Test",
                        "lastNameFather":"UserFather",
                        "lastNameMother":"UserMother",
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson))
                        .andExpect(status().isCreated());

                // Login
                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                var loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String token = objectMapper.readTree(
                        loginResult.getResponse().getContentAsString()
                ).get("token").asText();

                // Add item
                String addItemJson = """
                        {
                        "productId":"keyboard-100k",
                        "quantity":1
                        }
                        """;

                mockMvc.perform(post("/cart/items")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addItemJson))
                        .andExpect(status().isOk());

                // Checkout
                var checkoutResult = mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();

                Long orderId = objectMapper.readTree(
                        checkoutResult.getResponse().getContentAsString()
                ).get("orderId").asLong();

                // Pay
                mockMvc.perform(post("/orders/" + orderId + "/pay")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PAID"));

                // Validate DB state
                UserPoints userPoints = userPointsRepository
                        .findByUserId(
                                orderRepository.findById(orderId).orElseThrow().getUserId()
                        )
                        .orElseThrow();

                assertEquals(119000L, userPoints.getBalance());
                assertEquals(119000L, userPoints.getTotalEarned());

                // Validate level via /me
                mockMvc.perform(get("/me")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.points").value(119000))
                        .andExpect(jsonPath("$.level").value("BRONZE"));
        }





        @Test
        void cancel_order_should_restore_stock() throws Exception {

                String email = "restore_" + System.currentTimeMillis() + "@email.com";

                // Seed product with stock = 2
                var product = new com.theduckers.backend.entity.mongo.ProductDocument();
                product.setId("keyboard-restore");
                product.setName("Restore Keyboard");
                product.setPrice(50000L);
                product.setStock(2);
                mongoTemplate.save(product);

                // Register
                String registerJson = """
                        {
                        "firstName":"Test",
                        "lastNameFather":"UserFather",
                        "lastNameMother":"UserMother",
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson))
                        .andExpect(status().isCreated());

                // Login
                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                var loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String token = objectMapper.readTree(
                        loginResult.getResponse().getContentAsString()
                ).get("token").asText();

                // Add item quantity = 2
                String addItemJson = """
                        {
                        "productId":"keyboard-restore",
                        "quantity":2
                        }
                        """;

                mockMvc.perform(post("/cart/items")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addItemJson))
                        .andExpect(status().isOk());

                // Checkout → stock becomes 0
                var checkoutResult = mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();

                Long orderId = objectMapper.readTree(
                        checkoutResult.getResponse().getContentAsString()
                ).get("orderId").asLong();

                var afterCheckout = mongoTemplate.findById("keyboard-restore",
                        com.theduckers.backend.entity.mongo.ProductDocument.class);

                assertNotNull(afterCheckout);
                assertEquals(0, afterCheckout.getStock());

                // Cancel order
                mockMvc.perform(post("/orders/" + orderId + "/cancel")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("CANCELLED"));

                // Verify stock restored to 2
                var afterCancel = mongoTemplate.findById("keyboard-restore",
                        com.theduckers.backend.entity.mongo.ProductDocument.class);

                assertNotNull(afterCancel);
                assertEquals(2, afterCancel.getStock());
        }




        @Test
        void redemption_should_be_applied_only_after_payment() throws Exception {

                String email = "redeem_" + System.currentTimeMillis() + "@email.com";

                // Seed product worth 100000 CLP
                var product = new com.theduckers.backend.entity.mongo.ProductDocument();
                product.setId("keyboard-redeem");
                product.setName("Redeem Keyboard");
                product.setPrice(100000L);
                product.setStock(1);
                mongoTemplate.save(product);

                // Register user
                String registerJson = """
                        {
                        "firstName":"Test",
                        "lastNameFather":"UserFather",
                        "lastNameMother":"UserMother",
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson))
                        .andExpect(status().isCreated());

                // Login
                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                var loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String token = objectMapper.readTree(
                        loginResult.getResponse().getContentAsString()
                ).get("token").asText();

                // Fetch correct userId
                var user = userRepository.findByEmail(email).orElseThrow();
                Long userId = user.getId();

                // Manually grant 100000 points
                var userPoints = userPointsRepository.findByUserId(userId).orElseThrow();
                userPoints.setBalance(100000L);
                userPoints.setTotalEarned(100000L);
                userPointsRepository.save(userPoints);

                // Add item
                String addItemJson = """
                        {
                        "productId":"keyboard-redeem",
                        "quantity":1
                        }
                        """;

                mockMvc.perform(post("/cart/items")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addItemJson))
                        .andExpect(status().isOk());

                // Checkout WITH redeemPoints = true
                String checkoutJson = """
                        {
                        "redeemPoints": true
                        }
                        """;

                var checkoutResult = mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(checkoutJson))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.discount").value(1000))
                        .andExpect(jsonPath("$.iva").value(18810))
                        .andExpect(jsonPath("$.total").value(117810))
                        .andReturn();

                Long orderId = objectMapper.readTree(
                        checkoutResult.getResponse().getContentAsString()
                ).get("orderId").asLong();

                // Verify balance unchanged after checkout
                var beforePayment = userPointsRepository.findByUserId(userId).orElseThrow();
                assertEquals(100000L, beforePayment.getBalance());
                assertEquals(100000L, beforePayment.getTotalEarned());

                // Pay order
                mockMvc.perform(post("/orders/" + orderId + "/pay")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PAID"));

                // Verify final balance after deduction + emission
                var afterPayment = userPointsRepository.findByUserId(userId).orElseThrow();

                // 100000 - 100000 + 117810 = 117810
                assertEquals(117810L, afterPayment.getBalance());

                // totalEarned increased by emission (117810)
                assertEquals(217810L, afterPayment.getTotalEarned());

                // Verify redemption record exists
                var redemption = pointRedemptionRepository.findByOrderId(orderId);
                assertTrue(redemption.isPresent());

                assertEquals(100000L, redemption.get().getPointsUsed());
                assertEquals(1000L, redemption.get().getDiscountAmount());
        }





        @Test
        void redemption_should_be_capped_at_30_percent() throws Exception {

                String email = "cap_" + System.currentTimeMillis() + "@email.com";

                // Seed product worth 100000 CLP
                var product = new com.theduckers.backend.entity.mongo.ProductDocument();
                product.setId("keyboard-cap");
                product.setName("Cap Keyboard");
                product.setPrice(100000L);
                product.setStock(1);
                mongoTemplate.save(product);

                // Register
                String registerJson = """
                        {
                        "firstName":"Test",
                        "lastNameFather":"UserFather",
                        "lastNameMother":"UserMother",
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson))
                        .andExpect(status().isCreated());

                // Login
                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                var loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String token = objectMapper.readTree(
                        loginResult.getResponse().getContentAsString()
                ).get("token").asText();

                // Fetch userId
                var user = userRepository.findByEmail(email).orElseThrow();
                Long userId = user.getId();

                // Grant massive points
                var userPoints = userPointsRepository.findByUserId(userId).orElseThrow();
                userPoints.setBalance(10_000_000L);
                userPoints.setTotalEarned(10_000_000L);
                userPointsRepository.save(userPoints);

                // Add item
                String addItemJson = """
                        {
                        "productId":"keyboard-cap",
                        "quantity":1
                        }
                        """;

                mockMvc.perform(post("/cart/items")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addItemJson))
                        .andExpect(status().isOk());

                // Checkout with redemption
                String checkoutJson = """
                        {
                        "redeemPoints": true
                        }
                        """;

                var checkoutResult = mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(checkoutJson))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.discount").value(30000))
                        .andExpect(jsonPath("$.iva").value(13300))
                        .andExpect(jsonPath("$.total").value(83300))
                        .andReturn();

                Long orderId = objectMapper.readTree(
                        checkoutResult.getResponse().getContentAsString()
                ).get("orderId").asLong();

                // Pay
                mockMvc.perform(post("/orders/" + orderId + "/pay")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PAID"));

                // Verify final balance
                var afterPayment = userPointsRepository.findByUserId(userId).orElseThrow();

                // 10,000,000 - 3,000,000 + 83,300 = 7,083,300
                assertEquals(7_083_300L, afterPayment.getBalance());

                // totalEarned increased by emission (83,300)
                assertEquals(10_083_300L, afterPayment.getTotalEarned());

                // Verify redemption record
                var redemption = pointRedemptionRepository.findByOrderId(orderId);
                assertTrue(redemption.isPresent());
                assertEquals(3_000_000L, redemption.get().getPointsUsed());
                assertEquals(30_000L, redemption.get().getDiscountAmount());
        }





        @Test
        void duoc_user_should_receive_10_percent_discount_and_correct_vat() throws Exception {

                String email = "duoc_" + System.currentTimeMillis() + "@duocuc.cl";

                // Seed product worth 100000 CLP
                var product = new com.theduckers.backend.entity.mongo.ProductDocument();
                product.setId("keyboard-duoc");
                product.setName("DUOC Keyboard");
                product.setPrice(100000L);
                product.setStock(1);
                mongoTemplate.save(product);

                // Register DUOC user
                String registerJson = """
                        {
                        "firstName":"Test",
                        "lastNameFather":"UserFather",
                        "lastNameMother":"UserMother",
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson))
                        .andExpect(status().isCreated());

                // Login
                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                var loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String token = objectMapper.readTree(
                        loginResult.getResponse().getContentAsString()
                ).get("token").asText();

                // Add item to cart
                String addItemJson = """
                        {
                        "productId":"keyboard-duoc",
                        "quantity":1
                        }
                        """;

                mockMvc.perform(post("/cart/items")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addItemJson))
                        .andExpect(status().isOk());

                // Checkout (NO redemption)
                var checkoutResult = mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.subtotal").value(100000))
                        .andExpect(jsonPath("$.duocDiscount").value(10000))
                        .andExpect(jsonPath("$.pointsDiscount").value(0))
                        .andExpect(jsonPath("$.iva").value(17100))
                        .andExpect(jsonPath("$.total").value(107100))
                        .andReturn();

                Long orderId = objectMapper.readTree(
                        checkoutResult.getResponse().getContentAsString()
                ).get("orderId").asLong();

                // Pay order
                mockMvc.perform(post("/orders/" + orderId + "/pay")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PAID"));

                // Verify points emission
                var user = userRepository.findByEmail(email).orElseThrow();
                var userPoints = userPointsRepository.findByUserId(user.getId()).orElseThrow();

                assertEquals(107100L, userPoints.getBalance());
                assertEquals(107100L, userPoints.getTotalEarned());
        }




        @Test
        void duoc_user_with_redemption_should_apply_both_discounts_and_correct_vat() throws Exception {

                String email = "duoc_redeem_" + System.currentTimeMillis() + "@duocuc.cl";

                // Seed product worth 100000 CLP
                var product = new com.theduckers.backend.entity.mongo.ProductDocument();
                product.setId("keyboard-duoc-redeem");
                product.setName("DUOC Redeem Keyboard");
                product.setPrice(100000L);
                product.setStock(1);
                mongoTemplate.save(product);

                // Register DUOC user
                String registerJson = """
                        {
                        "firstName":"Test",
                        "lastNameFather":"UserFather",
                        "lastNameMother":"UserMother",
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson))
                        .andExpect(status().isCreated());

                // Login
                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                var loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String token = objectMapper.readTree(
                        loginResult.getResponse().getContentAsString()
                ).get("token").asText();

                // Fetch user and grant 1,000,000 points
                var user = userRepository.findByEmail(email).orElseThrow();
                Long userId = user.getId();

                var userPoints = userPointsRepository.findByUserId(userId).orElseThrow();
                userPoints.setBalance(1_000_000L);
                userPoints.setTotalEarned(1_000_000L);
                userPointsRepository.save(userPoints);

                // Add item
                String addItemJson = """
                        {
                        "productId":"keyboard-duoc-redeem",
                        "quantity":1
                        }
                        """;

                mockMvc.perform(post("/cart/items")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addItemJson))
                        .andExpect(status().isOk());

                // Checkout WITH redemption
                String checkoutJson = """
                        {
                        "redeemPoints": true
                        }
                        """;

                var checkoutResult = mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(checkoutJson))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.subtotal").value(100000))
                        .andExpect(jsonPath("$.duocDiscount").value(10000))
                        .andExpect(jsonPath("$.pointsDiscount").value(10000))
                        .andExpect(jsonPath("$.iva").value(15200))
                        .andExpect(jsonPath("$.total").value(95200))
                        .andReturn();

                Long orderId = objectMapper.readTree(
                        checkoutResult.getResponse().getContentAsString()
                ).get("orderId").asLong();

                // Pay
                mockMvc.perform(post("/orders/" + orderId + "/pay")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PAID"));

                // Verify points after payment
                var afterPayment = userPointsRepository.findByUserId(userId).orElseThrow();

                // 1,000,000 - 1,000,000 + 95,200 = 95,200
                assertEquals(95_200L, afterPayment.getBalance());

                // totalEarned increased by 95,200
                assertEquals(1_095_200L, afterPayment.getTotalEarned());

                // Verify redemption record
                var redemption = pointRedemptionRepository.findByOrderId(orderId);
                assertTrue(redemption.isPresent());
                assertEquals(1_000_000L, redemption.get().getPointsUsed());
                assertEquals(10_000L, redemption.get().getDiscountAmount());
        }


}