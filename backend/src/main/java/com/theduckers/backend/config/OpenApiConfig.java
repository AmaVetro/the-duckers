package com.theduckers.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// config/OpenApiConfig:

@Configuration
public class OpenApiConfig {

        public static final String SECURITY_SCHEME_NAME = "BearerAuth";

        @Bean
        public OpenAPI openAPI() {

                final String securitySchemeName = SECURITY_SCHEME_NAME;

                return new OpenAPI()
                        .info(new Info()
                                .title("The Duckers API")
                                .version("v1")
                                .description("""
                                The Duckers is a portfolio-grade e-commerce API built with Spring Boot.

                                Key features:
                                - Stateless JWT authentication (access token only)
                                - MySQL (transactions) + MongoDB (product catalog)
                                - Order lifecycle: PENDING → PAID → CANCELLED

                                Financial highlights:
                                - VAT (IVA) 19%
                                - DUOC discount: 10% for @duocuc.cl emails
                                - Loyalty system: 100 points = 1 CLP (max 30% redemption)

                                All endpoints are stateless and require JWT unless explicitly stated.

                                For more info, see the README.

                                User flow:
                                - Get/product, Get/categories, Get/health can be used without authentication
                                - If any other endpoint is used without authentication, a 401 error is returned
                                - Register endpoint creates a new user and returns a JWT access token, but this token is not meant to be used in "Authorize" button (the login one is)
                                - Then, the Login endpoint returns a JWT access token that must be used in the "Authorize" button
                                - After the login JWT access token is used in "Authorize", all the secured endpoints can be used
                                - Purcharse flow: Post/cart/items --> Post/checkout --> Post/orders/{id}/pay
                                """)
                        )
                        .components(new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                        );
        }
}