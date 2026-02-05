package com.theduckers.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.theduckers.backend.entity.UserPoints;
import com.theduckers.backend.repository.UserPointsRepository;

class PointsServiceTest {

    @Mock
    private UserPointsRepository userPointsRepository;

    private PointsService pointsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pointsService = new PointsService(userPointsRepository);
    }

    // tests vienen aquí
    @Test
    void addPoints_shouldIncreaseBalanceAndTotalEarned_whenValidAmount() {
        // GIVEN
        Long userId = 1L;
        long amount = 100;

        UserPoints userPoints = new UserPoints(userId);
        userPoints.setBalance(200L);
        userPoints.setTotalEarned(300L);

        when(userPointsRepository.findByUserId(userId))
                .thenReturn(Optional.of(userPoints));

        // WHEN
        pointsService.addPoints(userId, amount);

        // THEN
        assertEquals(300L, userPoints.getBalance());
        assertEquals(400L, userPoints.getTotalEarned());

        verify(userPointsRepository).save(userPoints);
    }



}
