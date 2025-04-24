package com.diner.backend.dto;

import com.diner.backend.entity.restaurant.Cuisine;
import com.diner.backend.entity.restaurant.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantDisplayDto {
    private Long restaurantId;
    private String restaurantName;
    private String description;
    private String email;
    private String phoneNumber;
    
    // Location information
    private String formattedAddress;
    private String city;
    private String area;
    private String landmark;
    private Double latitude;
    private Double longitude;
    
    // Media URLs
    private List<String> foodImageUrls;
    private List<String> menuImageUrls;
    private List<String> diningImageUrls;
    
    // Cuisine and operating info
    private List<Cuisine> cuisines;
    private String openingTime;
    private String closingTime;
    private List<DayOfWeek> openingDays;
    
    // Rating and status
    private Double averageRating;
    private Boolean isActive;
    
    // Derived fields for better UX
    private String operatingHours;
    private String shortDescription;



}