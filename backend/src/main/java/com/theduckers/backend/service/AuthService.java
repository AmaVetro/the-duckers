package com.theduckers.backend.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.theduckers.backend.dto.auth.AuthResponse;
import com.theduckers.backend.dto.auth.LoginRequest;
import com.theduckers.backend.dto.auth.RegisterRequest;
import com.theduckers.backend.entity.User;
import com.theduckers.backend.repository.UserRepository;
import com.theduckers.backend.security.JwtService;
import com.theduckers.backend.security.UserDetailsImpl;
import com.theduckers.backend.repository.UserPointsRepository;
import com.theduckers.backend.entity.UserPoints;
import com.theduckers.backend.exception.BadRequestException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserPointsRepository userPointsRepository;
    private final ReferralService referralService;


    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserPointsRepository userPointsRepository,
            ReferralService referralService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userPointsRepository = userPointsRepository;
        this.referralService = referralService;
    }




    // =========================
    // REGISTER
    // =========================
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already in use");
        }

        User user = new User(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFirstName(),
            request.getLastNameFather(),
            request.getLastNameMother(),
            generateReferralCode(),
            LocalDateTime.now()
        );

        userRepository.save(user);
        UserPoints userPoints = new UserPoints(user.getId());
        userPointsRepository.save(userPoints);

        if (request.getReferralCode() != null && !request.getReferralCode().isBlank()) {
            referralService.processReferral(request.getReferralCode(), user);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                token,
                Instant.now().plusSeconds(60 * 60)
        );
    }





    
    // =========================
    // LOGIN
    // =========================
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadRequestException("Invalid credentials")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new BadRequestException("Invalid credentials");
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                token,
                Instant.now().plusSeconds(60 * 60)
        );
    }

    // =========================
    // INTERNAL UTIL
    // =========================
    private String generateReferralCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
