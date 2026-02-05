package com.theduckers.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.theduckers.backend.entity.UserPoints;
import com.theduckers.backend.repository.UserPointsRepository;
import com.theduckers.backend.exception.BadRequestException;
import com.theduckers.backend.exception.InvalidStateException;


@Service
public class PointsService {

    private final UserPointsRepository userPointsRepository;

    public PointsService(UserPointsRepository userPointsRepository) {
        this.userPointsRepository = userPointsRepository;
    }

    @Transactional
    public void addPoints(Long userId, long amount) {

        if (amount <= 0) {
            throw new BadRequestException("Points amount must be positive");
        }

        UserPoints userPoints = userPointsRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new InvalidStateException("UserPoints not found for user " + userId)
                );

        userPoints.setBalance(userPoints.getBalance() + amount);
        userPoints.setTotalEarned(userPoints.getTotalEarned() + amount);
        userPoints.touchUpdatedAt();

        userPointsRepository.save(userPoints);
    }
}
