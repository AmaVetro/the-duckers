package com.theduckers.backend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.theduckers.backend.dto.auth.UserProfileResponse;
import com.theduckers.backend.security.UserDetailsImpl;
import com.theduckers.backend.entity.UserPoints;
import com.theduckers.backend.repository.UserPointsRepository;
import com.theduckers.backend.service.LevelService;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.theduckers.backend.config.OpenApiConfig.SECURITY_SCHEME_NAME;

@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RestController
public class ProfileController {

        private final UserPointsRepository userPointsRepository;
        private final LevelService levelService;

        public ProfileController(
                UserPointsRepository userPointsRepository,
                LevelService levelService
        ) {
                this.userPointsRepository = userPointsRepository;
                this.levelService = levelService;
        }

        @GetMapping("/me")
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
        public UserProfileResponse getProfile(
                @AuthenticationPrincipal UserDetailsImpl userDetails
        ) {
                UserPoints userPoints = userPointsRepository.findByUserId(userDetails.getId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User profile data not found"
                                )
                        );

                String levelName = levelService
                        .getLevelForTotalPoints(userPoints.getTotalEarned())
                        .getName();

                return new UserProfileResponse(
                        userDetails.getId(),
                        userDetails.getUsername(),
                        levelName,
                        userPoints.getBalance()
                );
        }
}
