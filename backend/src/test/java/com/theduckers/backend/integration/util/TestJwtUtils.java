package com.theduckers.backend.integration.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//test/integration/util/TestJwtUtils:



public class TestJwtUtils {

        private TestJwtUtils() {
                // Utility class
        }




        /**
         * Registers a new valid user for integration tests.
         */
        public static void registerUser(
                MockMvc mockMvc,
                ObjectMapper objectMapper,
                String email,
                String password
        ) throws Exception {

                RegisterRequest request = new RegisterRequest(
                        "Test",
                        "UserFather",
                        "UserMother",
                        email,
                        password
                );

                String requestBody = objectMapper.writeValueAsString(request);

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                        .andExpect(status().isCreated());
        }




        /**
         * Performs login and returns JWT token.
         */
        public static String loginAndGetToken(
                MockMvc mockMvc,
                ObjectMapper objectMapper,
                String email,
                String password
        ) throws Exception {

                LoginRequest request = new LoginRequest(email, password);

                String requestBody = objectMapper.writeValueAsString(request);

                MvcResult result = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                        .andExpect(status().isOk())
                        .andReturn();

                String responseContent = result.getResponse().getContentAsString();

                JsonNode jsonNode = objectMapper.readTree(responseContent);

                return jsonNode.get("token").asText();
        }




        /**
         * Test-only DTO matching real RegisterRequest.
         */
        private record RegisterRequest(
                String firstName,
                String lastNameFather,
                String lastNameMother,
                String email,
                String password
        ) {}




        /**
         * Test-only DTO matching real LoginRequest.
         */
        private record LoginRequest(
                String email,
                String password
        ) {}
}