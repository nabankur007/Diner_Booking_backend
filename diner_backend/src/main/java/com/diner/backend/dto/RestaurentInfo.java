package com.diner.backend.dto;

import com.diner.backend.entity.restaurant.Cuisine;
import com.diner.backend.entity.restaurant.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurentInfo {
    private String restaurant_name;
    private String description;
    private String phoneNumber;
    private String email;

    private String openingTime;
    private String closingTime;

    private List<String> menuImageUrls;
    private List<String> foodImageUrls;
    private List<String> diningImageUrls;

    private List<Cuisine> cuisines;
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
