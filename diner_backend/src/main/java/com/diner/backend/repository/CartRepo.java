package com.diner.backend.repository;

import com.diner.backend.entity.restaurant.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Cart, Long> {
}
