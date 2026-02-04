package com.theduckers.backend.repository;

import com.theduckers.backend.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByReferralCode(String referralCode);

}
