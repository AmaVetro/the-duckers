package com.theduckers.backend.integration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theduckers.backend.integration.util.TestJwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




//test/integration/CartIntegrationTest:


public class CartIntegrationTest extends AbstractIntegrationTest {

    @Test
    void addItem_should_add_product_to_cart_and_return_correct_snapshot() throws Exception {

        // =========================
        // 1️⃣ Seed Mongo manually
        // =========================

        var product = new org.bson.Document()
                .append("_id", "keyboard-001")
                .append("name", "Mechanical Keyboard")
                .append("description", "RGB Mechanical Keyboard")
                .append("price", 49990L)
                .append("stock", 10)
                .append("categoryId", "keyboards");

        mongoTemplate.getDb().getCollection("products").insertOne(product);

        // =========================
        // 2️⃣ Register + Login
        // =========================

        String email = "cartuser@email.com";
        String password = "Password123";

        TestJwtUtils.registerUser(mockMvc, objectMapper, email, password);
        String token = TestJwtUtils.loginAndGetToken(mockMvc, objectMapper, email, password);

        // =========================
        // 3️⃣ Prepare request body
        // =========================

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("productId", "keyboard-001");
        requestBody.put("quantity", 2);

        // =========================
        // 4️⃣ Perform POST /cart/items
        // =========================

        mockMvc.perform(post("/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.items[0].productId").value("keyboard-001"))
                .andExpect(jsonPath("$.items[0].productName").value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(49990))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].subtotal").value(99980))
                .andExpect(jsonPath("$.subtotal").value(99980));
    }

    @Test
    void addItem_should_return_401_when_missing_token() throws Exception {

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("productId", "keyboard-001");
        requestBody.put("quantity", 1);

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized());
    }
}