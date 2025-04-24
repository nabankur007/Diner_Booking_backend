package com.diner.backend.entity.restaurant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Entity representing a shopping cart in the restaurant system.
 * The cart is associated with a specific restaurant and can have multiple bookings.
 */
@Entity
@Table(name = "cart")
@Data
public class Cart {

    /**
     * Unique identifier for the cart.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id", nullable = false, updatable = false)
    private Long id;  // Changed from int to Long for better compatibility

    /**
     * The restaurant associated with the cart.
     * Represents a foreign key to the 'restaurants' table.
     */
    @NotNull(message = "Restaurant name cannot be null")
    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;


}
