package com.diner.backend.service;


import com.diner.backend.entity.Booking;

import java.util.List;

public interface BookingService {
    List<Booking> getAllBookings();

    Booking createBooking(Booking booking);

    List<Booking> getBookingsByUsername(String username);

    List<Booking> getBookingsByRestaurantName(String restaurantName);
}


