package com.theduckers.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                                .description("Backend API for The Duckers e-commerce project. Portfolio-oriented Spring Boot application with MySQL and MongoDB.")
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
