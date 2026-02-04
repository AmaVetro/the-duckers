package com.theduckers.backend.service;

import org.springframework.stereotype.Service;

import com.theduckers.backend.entity.Referral;
import com.theduckers.backend.entity.User;
import com.theduckers.backend.exception.BadRequestException;
import com.theduckers.backend.repository.ReferralRepository;
import com.theduckers.backend.repository.UserRepository;

@Service
public class ReferralService {

    private static final long REFERRAL_BONUS_POINTS = 500;

    private final UserRepository userRepository;
    private final ReferralRepository referralRepository;
    private final PointsService pointsService;

    public ReferralService(
            UserRepository userRepository,
            ReferralRepository referralRepository,
            PointsService pointsService
    ) {
        this.userRepository = userRepository;
        this.referralRepository = referralRepository;
        this.pointsService = pointsService;
    }

    public void processReferral(String referralCode, User newUser) {

        User referrer = userRepository.findByReferralCode(referralCode)
                .orElseThrow(() ->
                        new BadRequestException("Invalid referral code")
                );

        if (referrer.getId().equals(newUser.getId())) {
            throw new BadRequestException("Self-referral is not allowed");
        }

        if (referralRepository.existsByReferredUserId(newUser.getId())) {
            throw new BadRequestException("User has already been referred");
        }

        // Registrar quién refirió al usuario
        newUser.setReferredByUserId(referrer.getId());
        userRepository.save(newUser);

        Referral referral = new Referral(
                referrer.getId(),
                newUser.getId(),
                REFERRAL_BONUS_POINTS
        );

        referralRepository.save(referral);

        pointsService.addPoints(referrer.getId(), REFERRAL_BONUS_POINTS);
        pointsService.addPoints(newUser.getId(), REFERRAL_BONUS_POINTS);
    }
}
