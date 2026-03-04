package com.theduckers.backend.controller;

import com.theduckers.backend.dto.auth.UserProfileResponse;
import com.theduckers.backend.security.UserDetailsImpl;
import com.theduckers.backend.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.theduckers.backend.config.OpenApiConfig.SECURITY_SCHEME_NAME;

//controller/ProfileController:

@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RestController
public class ProfileController {

        private final UserProfileService userProfileService;

        public ProfileController(UserProfileService userProfileService) {
                this.userProfileService = userProfileService;
        }

        @Operation(
                summary = "Get authenticated user profile",
                description = """
                        Returns the profile of the authenticated user.
                        - Current loyalty point balance
                        - Dynamically calculated reputation level
                        - Username
                        Levels are reputational only and have no financial impact.
                        """
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Authenticated user profile",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = UserProfileResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized – missing or invalid JWT",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "User profile data not found",
                        content = @Content
                )
        })
        @GetMapping("/me")
        public UserProfileResponse getProfile(
                @AuthenticationPrincipal UserDetailsImpl userDetails
        ) {
                return userProfileService.getProfile(userDetails);
        }
}