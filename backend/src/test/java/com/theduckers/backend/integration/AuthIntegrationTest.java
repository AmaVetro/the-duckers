package com.theduckers.backend.integration;

import com.theduckers.backend.integration.util.TestJwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



//test/integration/AuthIntegrationTest:


public class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    void register_and_login_should_return_valid_jwt_and_access_protected_endpoint() throws Exception {

        String email = "testuser@email.com";
        String password = "Password123";

        // Register user
        TestJwtUtils.registerUser(mockMvc, objectMapper, email, password);

        // Login and extract token
        String token = TestJwtUtils.loginAndGetToken(mockMvc, objectMapper, email, password);

        // Ensure token is not null
        assert token != null && !token.isBlank();

        // Access protected endpoint with token
        mockMvc.perform(get("/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }
}