package com.theduckers.backend.repository;

import com.theduckers.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

//Repository/OrderItemRepository:

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
