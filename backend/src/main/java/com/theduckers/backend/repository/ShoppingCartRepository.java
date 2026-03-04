package com.theduckers.backend.repository;

import com.theduckers.backend.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


//Repository/ShoppingCartRepository:



public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    /**
     * Business rule:
     * - There can be at most one ACTIVE cart per user.
     */
    Optional<ShoppingCart> findByUserIdAndStatus(Long userId, String status);

    /**
     * Checks whether a user already has an ACTIVE cart.
     *
     * Used to enforce the rule:
     * - One active cart per user.
     */
    boolean existsByUserIdAndStatus(Long userId, String status);


    @Query("""
        SELECT c
        FROM ShoppingCart c
        LEFT JOIN FETCH c.items
        WHERE c.id = :id
    """)
    Optional<ShoppingCart> findByIdWithItems(Long id);
}
