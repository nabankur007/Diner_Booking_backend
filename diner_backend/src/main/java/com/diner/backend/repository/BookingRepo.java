package com.diner.backend.repository;

import com.diner.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long> {
    // Query bookings by username
    List<Booking> findByUsername(String username);

    // Query bookings by restaurant name
    List<Booking> findByRestaurantName(String restaurantName);
}
