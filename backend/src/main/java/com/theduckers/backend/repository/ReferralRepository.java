package com.theduckers.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.theduckers.backend.entity.Referral;



//Repository/ReferralRepository:



public interface ReferralRepository extends JpaRepository<Referral, Long> {

    boolean existsByReferredUserId(Long referredUserId);
}
