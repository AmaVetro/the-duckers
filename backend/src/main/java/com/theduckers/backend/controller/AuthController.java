package com.theduckers.backend.controller;

import com.theduckers.backend.dto.auth.AuthResponse;
import com.theduckers.backend.dto.auth.LoginRequest;
import com.theduckers.backend.dto.auth.RegisterRequest;
import com.theduckers.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


//controller/AuthController:

@RestController
@RequestMapping("/auth")
public class AuthController {

        private final AuthService authService;

        public AuthController(AuthService authService) {
                this.authService = authService;
        }

        // =========================
        // REGISTER
        // =========================
        @Operation(
                summary = "Register new user",
                description = """
                        Creates a new user account and returns a JWT access token.
                        - Referral code from other user may be provided to earn points (Referrer and Refered user both earn 700.000 points).
                        - Email must contain @ and a domain (.cl, .com, etc), and not be already registered.
                        - Password must be at least 8 characters, contain at least one uppercase letter, one lowercase letter, and one digit.
                        Important:
                        - If a referral code is not provided, "string" must be replaced with null. Example: "referralCode": null
                        - Do not use the JWT access token provided in the response for the "Authorize" button (the Post/auth/login JWT access token is needed for that).
                        """
        )                     
        @PostMapping("/register")
        @ResponseStatus(HttpStatus.CREATED)
        @ApiResponses({
                @ApiResponse(
                        responseCode = "201",
                        description = "User registered successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = AuthResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input or business rule violation",
                        content = @Content
                )
        })
        public AuthResponse register(
                @Valid @RequestBody RegisterRequest request
        ) {
                return authService.register(request);
        }

        // =========================
        // LOGIN
        // =========================
        @Operation(
                summary = "Authenticate user",
                description = "Authenticates user credentials and returns a JWT access token that can be used in Authorize button. The API is fully stateless and does not use refresh tokens."
        )
        @PostMapping("/login")
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "User authenticated successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = AuthResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request body",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Invalid credentials",
                        content = @Content
                )
        })
        public AuthResponse login(
                @Valid @RequestBody LoginRequest request
        ) {
                return authService.login(request);
        }
}