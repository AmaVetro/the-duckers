package com.theduckers.backend.service;

import com.theduckers.backend.dto.auth.UserProfileResponse;
import com.theduckers.backend.entity.User;
import com.theduckers.backend.entity.UserPoints;
import com.theduckers.backend.repository.UserPointsRepository;
import com.theduckers.backend.repository.UserRepository;
import com.theduckers.backend.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;




//service/UserProfileService:

@Service
public class UserProfileService {

        private final UserPointsRepository userPointsRepository;
        private final LevelService levelService;
        private final UserRepository userRepository;

        public UserProfileService(
                UserPointsRepository userPointsRepository,
                LevelService levelService,
                UserRepository userRepository
        ) {
                this.userPointsRepository = userPointsRepository;
                this.levelService = levelService;
                this.userRepository = userRepository;
}

        public UserProfileResponse getProfile(UserDetailsImpl userDetails) {

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

                User user = userRepository.findById(userDetails.getId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found"
                                )
                        );

                return new UserProfileResponse(
                        userDetails.getId(),
                        userDetails.getUsername(),
                        levelName,
                        userPoints.getBalance(),
                        user.getReferralCode()
                );
        }
}