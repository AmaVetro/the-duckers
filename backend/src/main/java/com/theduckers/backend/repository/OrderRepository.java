package com.theduckers.backend.repository;

import com.theduckers.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


//Repository/OrderRepository:


public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);



}
