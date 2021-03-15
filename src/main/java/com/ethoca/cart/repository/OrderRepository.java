package com.ethoca.cart.repository;

import com.ethoca.cart.model.db.OrderConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderConfirmation, Integer> {
}
