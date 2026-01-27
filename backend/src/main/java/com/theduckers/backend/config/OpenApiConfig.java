package com.theduckers.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI theDuckersOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("The Duckers API")
                .version("v1")
                .description(
                    "Backend API for The Duckers e-commerce project. " +
                    "Portfolio-oriented Spring Boot application with MySQL and MongoDB."
                )
            );
    }
}
