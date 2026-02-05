package com.theduckers.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.theduckers.backend.entity.Referral;
import com.theduckers.backend.entity.User;
import com.theduckers.backend.exception.BadRequestException;
import com.theduckers.backend.repository.ReferralRepository;
import com.theduckers.backend.repository.UserRepository;

class ReferralServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReferralRepository referralRepository;

    @Mock
    private PointsService pointsService;

    @InjectMocks
    private ReferralService referralService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void shouldThrowBadRequestException_whenReferralCodeIsInvalid() {
        // ARRANGE
        String invalidReferralCode = "INVALID123";

        User newUser = mock(User.class);
        when(newUser.getId()).thenReturn(2L);

        when(userRepository.findByReferralCode(invalidReferralCode))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> referralService.processReferral(invalidReferralCode, newUser)
        );

        assertEquals("Invalid referral code", exception.getMessage());

        verify(referralRepository, never()).save(any());
        verify(pointsService, never()).addPoints(anyLong(), anyLong());
    }

    @Test
    void shouldThrowBadRequestException_whenUserRefersHimself() {
        // ARRANGE
        String referralCode = "SELF123";

        User referrer = mock(User.class);
        when(referrer.getId()).thenReturn(1L);

        User newUser = mock(User.class);
        when(newUser.getId()).thenReturn(1L); // mismo ID → self-referral

        when(userRepository.findByReferralCode(referralCode))
                .thenReturn(Optional.of(referrer));

        // ACT + ASSERT
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> referralService.processReferral(referralCode, newUser)
        );

        assertEquals("Self-referral is not allowed", exception.getMessage());

        verify(referralRepository, never()).save(any());
        verify(pointsService, never()).addPoints(anyLong(), anyLong());
    }

    @Test
    void shouldThrowBadRequestException_whenUserAlreadyReferred() {
        // ARRANGE
        String referralCode = "REF123";

        User referrer = mock(User.class);
        when(referrer.getId()).thenReturn(1L);

        User newUser = mock(User.class);
        when(newUser.getId()).thenReturn(2L);

        when(userRepository.findByReferralCode(referralCode))
                .thenReturn(Optional.of(referrer));

        when(referralRepository.existsByReferredUserId(2L))
                .thenReturn(true);

        // ACT + ASSERT
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> referralService.processReferral(referralCode, newUser)
        );

        assertEquals("User has already been referred", exception.getMessage());

        verify(referralRepository, never()).save(any());
        verify(pointsService, never()).addPoints(anyLong(), anyLong());
    }


    @Test
    void shouldProcessReferralSuccessfully_whenDataIsValid() {
        // ARRANGE
        String referralCode = "REF123";

        User referrer = mock(User.class);
        when(referrer.getId()).thenReturn(1L);

        User newUser = mock(User.class);
        when(newUser.getId()).thenReturn(2L);

        when(userRepository.findByReferralCode(referralCode))
                .thenReturn(Optional.of(referrer));

        when(referralRepository.existsByReferredUserId(2L))
                .thenReturn(false);

        // ACT
        referralService.processReferral(referralCode, newUser);

        // ASSERT
        verify(newUser).setReferredByUserId(1L);
        verify(userRepository).save(newUser);

        verify(referralRepository).save(any(Referral.class));

        verify(pointsService).addPoints(1L, 500);
        verify(pointsService).addPoints(2L, 500);
    }







}
