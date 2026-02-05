package com.theduckers.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

public class AuthIntegrationTest extends AbstractIntegrationTest {

        @LocalServerPort
        private int port;

        private WebTestClient webTestClient;

        @BeforeEach
        void setupClient() {
                this.webTestClient = WebTestClient.bindToServer()
                        .baseUrl("http://localhost:" + port)
                        .build();
        }

        @Test
        void contextLoads() {
                // test mínimo: solo validar que el contexto levanta
        }

        @Test
        void register_shouldReturn201AndToken() {
                String requestBody = """
                        {
                        "email": "testuser1@duckers.cl",
                        "password": "password123",
                        "firstName": "Test",
                        "lastNameFather": "User",
                        "lastNameMother": "Duck",
                        "referralCode": null
                        }
                        """;

                webTestClient.post()
                        .uri("/auth/register")
                        .header("Content-Type", "application/json")
                        .bodyValue(requestBody)
                        .exchange()
                        .expectStatus().isCreated()
                        .expectHeader().contentTypeCompatibleWith("application/json")
                        .expectBody()
                        .jsonPath("$.token").exists()
                        .jsonPath("$.expiresAt").exists();
                }
}
