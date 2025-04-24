package com.diner.backend.repository;

import com.diner.backend.dto.RestaurantCard;
import com.diner.backend.entity.restaurant.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepo extends JpaRepository<Restaurants, Long> {

    public Optional<Restaurants> findByEmail(String restaurantName);

    @Query("SELECT r FROM Restaurants r WHERE LOWER(r.restaurantName) = LOWER(:name)")
    Optional<Restaurants> findByRestaurantNameIgnoreCase(@Param("name") String name);

    // Query to check if the restaurant exists by name
    boolean existsByRestaurantName(String restaurantName);


}
