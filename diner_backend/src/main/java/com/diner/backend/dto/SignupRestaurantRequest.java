package com.diner.backend.dto;

import com.diner.backend.entity.restaurant.Cuisine;
import com.diner.backend.entity.restaurant.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * DTO for restaurant signup request (no validations).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRestaurantRequest {

    private String userName;
    private String restaurantName;
    private String password;
    private String description;
    private String phoneNumber;
    private String email;

    private String openingTime;
    private String closingTime;

    private List<String> menuImageUrls;
    private List<String> foodImageUrls;
    private List<String> diningImageUrls;

    private List<Cuisine> cuisines;
    private Set<String> role;
    private List<DayOfWeek> openingDays;

    // Optional address fields
    private String shopNoBuildingNo;
    private String tower;
    private String area;
    private String city;
    private String landmark;
    private Double latitude;
    private Double longitude;
}
