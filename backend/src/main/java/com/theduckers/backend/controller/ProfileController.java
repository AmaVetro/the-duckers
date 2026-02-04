package com.theduckers.backend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.theduckers.backend.dto.auth.UserProfileResponse;
import com.theduckers.backend.security.UserDetailsImpl;

import com.theduckers.backend.entity.UserPoints;
import com.theduckers.backend.repository.UserPointsRepository;
import com.theduckers.backend.service.LevelService;

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
        public UserProfileResponse getProfile(
                @AuthenticationPrincipal UserDetailsImpl userDetails
        ) {
                UserPoints userPoints = userPointsRepository.findByUserId(userDetails.getId())
                        .orElseThrow(() ->
                                new IllegalStateException("UserPoints not found for authenticated user")
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
