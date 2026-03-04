package com.theduckers.backend.repository;

import com.theduckers.backend.entity.PointRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;



//repository/PointRedemptionRepository:

public interface PointRedemptionRepository
        extends JpaRepository<PointRedemption, Long> {

    Optional<PointRedemption> findByOrderId(Long orderId);
}