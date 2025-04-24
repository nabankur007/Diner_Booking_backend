package com.diner.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @NotBlank(message = "Username is required")
    @Column(name = "username")
    private String username;

    @NotBlank(message = "Restaurant name is required")
    @Column(name = "restaurant_name")
    private String restaurantName;

    @NotBlank(message = "Booking date is required")
    private String bookingDate; // Expected format: YYYY-MM-DD

    @NotBlank(message = "Booking time is required")
    private String bookingTime; // Expected format: HH:mm:ss

    @Min(value = 1, message = "Guest count must be at least 1")
    @Max(value = 16, message = "Guest count cannot exceed 16")
    private int guestCount;

    private String specialRequests;

    @NotNull(message = "Booking status is required")
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}
