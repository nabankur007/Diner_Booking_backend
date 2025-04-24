package com.diner.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
public class RestaurantCard {
    private String restaurantImageUrl;
    private String restaurantName;
    private double avgRating;
    private List<String> cuisines;
    private String openingTime;
    private String city;

    // Constructors, Getters and Setters

    public RestaurantCard() {}

    public RestaurantCard(String restaurantImageUrl, String restaurantName, double avgRating, List<String> cuisines, String openingTime,String city) {
        this.restaurantImageUrl = restaurantImageUrl;
        this.restaurantName = restaurantName;
        this.avgRating = avgRating;
        this.cuisines = cuisines;
        this.openingTime = openingTime;
        this.city=city;
    }
}
