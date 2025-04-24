package com.diner.backend.entity.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a restaurant entity with comprehensive details.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "restaurants")
public class Restaurants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id", nullable = false, updatable = false)
    private Long id;  // Changed from int to Long for better compatibility

    @Size(max = 120, message = "Password must not exceed 120 characters")
    @Column(name = "password")
    @JsonIgnore
    private String password;

    @NotBlank(message = "Restaurant name cannot be blank")
    @Size(max = 100, message = "Restaurant name must not exceed 100 characters")
    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "latitude")
    private Double latitude;  // Changed to Double for precision

    @Column(name = "longitude")
    private Double longitude;  // Changed to Double for precision

    @NotBlank(message = "Shop/Building number cannot be blank")
    @Column(name = "shop_no_building_no")
    private String shopNoBuildingNo;

    @Column(name = "tower")
    private String tower;

    @NotBlank(message = "Area cannot be blank")
    @Column(name = "area")
    private String area;

    @NotBlank(message = "City cannot be blank")
    @Column(name = "city")
    private String city;

    @Column(name = "landmark")
    private String landmark;

    @ElementCollection
    @CollectionTable(name = "restaurant_food_images", joinColumns = @JoinColumn(name = "restaurant_id"))
    @Column(name = "image_url")
    private List<String> foodImageUrls;

    @ElementCollection
    @CollectionTable(name = "restaurant_menu_images", joinColumns = @JoinColumn(name = "restaurant_id"))
    @Column(name = "image_url")
    private List<String> menuImageUrls;

    @ElementCollection
    @CollectionTable(name = "restaurant_dining_images", joinColumns = @JoinColumn(name = "restaurant_id"))
    @Column(name = "image_url")
    private List<String> diningImageUrls;

    @ElementCollection
    @CollectionTable(name = "restaurant_cuisines", joinColumns = @JoinColumn(name = "restaurant_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "cuisine_type")
    private List<Cuisine> cuisines;

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format (HH:MM)")
    @Column(name = "opening_time")
    private String openingTime;

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format (HH:MM)")
    @Column(name = "closing_time")

    private String closingTime;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", referencedColumnName = "cart_id")  // Referencing cart_id
    private Cart cart;


    @ElementCollection
    @CollectionTable(name = "restaurant_opening_days", joinColumns = @JoinColumn(name = "restaurant_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private List<DayOfWeek> openingDays;

    // Additional useful fields
    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @Column(name = "average_rating", columnDefinition = "double default 0.0")
    private Double averageRating = 0.0;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new java.util.Date();
        updatedAt = new java.util.Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new java.util.Date();
    }
}