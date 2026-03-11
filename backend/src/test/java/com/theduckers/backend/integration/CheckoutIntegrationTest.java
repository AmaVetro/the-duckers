package com.theduckers.backend.integration;

import com.theduckers.backend.entity.mongo.ProductDocument;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



//test/integration/CheckoutIntegrationTest:


public class CheckoutIntegrationTest extends AbstractIntegrationTest {

        @Autowired
        private MongoTemplate mongoTemplate;

        private final String TEST_PASSWORD = "Password123";

        @BeforeEach
        void cleanMongo() {
                mongoTemplate.dropCollection("products");
        }

        @Test
        void checkout_should_create_order_and_reduce_stock() throws Exception {

                // =========================
                // Generate unique email
                // =========================
                String email = "checkout_" + System.currentTimeMillis() + "@email.com";

                // =========================
                // 1️⃣ Seed product with stock = 2
                // =========================
                ProductDocument product = new ProductDocument();
                product.setId("keyboard-001");
                product.setName("Mechanical Keyboard");
                product.setPrice(50000L);
                product.setStock(2);

                mongoTemplate.save(product);

                // =========================
                // 2️⃣ Register user
                // =========================
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

                // =========================
                // 3️⃣ Login
                // =========================
                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                MvcResult loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String loginResponse = loginResult.getResponse().getContentAsString();
                String token = objectMapper.readTree(loginResponse).get("token").asText();

                // =========================
                // 4️⃣ Add item to cart (quantity = 2)
                // =========================
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

                // =========================
                // 5️⃣ POST /checkout
                // =========================
                mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("PENDING"))
                        .andExpect(jsonPath("$.subtotal").value(100000))
                        .andExpect(jsonPath("$.iva").value(19000))
                        .andExpect(jsonPath("$.total").value(119000))
                        .andExpect(jsonPath("$.items[0].productId").value("keyboard-001"))
                        .andExpect(jsonPath("$.items[0].quantity").value(2));

                // =========================
                // 6️⃣ Verify Mongo stock = 0
                // =========================
                ProductDocument updated = mongoTemplate.findById("keyboard-001", ProductDocument.class);

                assert updated != null;
                assert updated.getStock() == 0;
        }

        @Test
        void checkout_should_fail_and_restore_stock_if_insufficient() throws Exception {

                // =========================
                // Generate unique email
                // =========================
                String email = "checkout_" + System.currentTimeMillis() + "@email.com";

                // =========================
                // 1️⃣ Seed product with stock = 1
                // =========================
                ProductDocument product = new ProductDocument();
                product.setId("keyboard-001");
                product.setName("Mechanical Keyboard");
                product.setPrice(50000L);
                product.setStock(1);

                mongoTemplate.save(product);

                // =========================
                // 2️⃣ Register
                // =========================
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

                // =========================
                // 3️⃣ Login
                // =========================
                String loginJson = """
                        {
                        "email":"%s",
                        "password":"%s"
                        }
                        """.formatted(email, TEST_PASSWORD);

                MvcResult loginResult = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isOk())
                        .andReturn();

                String token = objectMapper.readTree(
                        loginResult.getResponse().getContentAsString()
                ).get("token").asText();

                // =========================
                // 4️⃣ Add quantity = 2 (exceeds stock)
                // =========================
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

                // =========================
                // 5️⃣ Checkout → expect 400
                // =========================
                mockMvc.perform(post("/checkout")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isBadRequest());

                // =========================
                // 6️⃣ Stock must remain 1
                // =========================
                ProductDocument updated = mongoTemplate.findById("keyboard-001", ProductDocument.class);

                assert updated != null;
                assert updated.getStock() == 1;
        }



        @Test
        void checkout_should_return_401_when_no_token_provided() throws Exception {

                mockMvc.perform(post("/checkout"))
                        .andExpect(status().isUnauthorized());
        }

}