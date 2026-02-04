package com.theduckers.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.theduckers.backend.entity.UserPoints;

public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {

    Optional<UserPoints> findByUserId(Long userId);
}
