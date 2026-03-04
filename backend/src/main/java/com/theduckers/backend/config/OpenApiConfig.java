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
                                The Duckers is a portfolio-grade e-commerce backend built with Spring Boot.

                                Architecture:
                                - Stateless REST API
                                - JWT access-token authentication (no refresh tokens)
                                - MySQL for transactional data (users, orders, points, referrals)
                                - MongoDB for flexible product catalog
                                - No database-to-database synchronization

                                Financial Model (Chile-compliant):

                                Order Calculation Flow:
                                1. subtotal = sum(order items)
                                2. DUOC discount = 10% of subtotal (if email ends with @duocuc.cl)
                                3. Points redemption = 100 points = 1 CLP (optional, capped at 30% of subtotal)
                                4. taxable base = subtotal - duocDiscount - pointsDiscount
                                5. VAT (IVA) = 19% applied over taxable base
                                6. total = taxable base + VAT

                                Safeguards:
                                - totalDiscount never exceeds subtotal
                                - taxable base is never negative
                                - VAT is always calculated over post-discount base
                                - Points are deducted only after successful payment
                                - Points are emitted over the final total INCLUDING VAT

                                Loyalty System:
                                - Emission: 1 CLP spent = 1 point
                                - Redemption: 100 points = 1 CLP
                                - Levels are reputational only (no financial impact)

                                This API intentionally avoids overengineering:
                                - No ledger system
                                - No async/event-driven complexity
                                - No refresh-token rotation
                                - Monetary values handled as long (CLP simplification)
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