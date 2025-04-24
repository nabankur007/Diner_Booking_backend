package com.diner.backend.service.serviceimpl;

import com.diner.backend.entity.Booking;
import com.diner.backend.repository.BookingRepo;
import com.diner.backend.repository.RestaurantRepo;

import com.diner.backend.repository.UsersRepo;
import com.diner.backend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private  BookingRepo bookingRepository;
    @Autowired
    private  UsersRepo userRepository;  // To verify if the user exists by username
    @Autowired
    private  RestaurantRepo restaurantRepository;  // To verify if the restaurant exists by restaurantName

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking createBooking(Booking booking) {
        // Verify that the user exists by username
        if (!userRepository.existsByUserName(booking.getUsername())) {
            throw new RuntimeException("User not found with username: " + booking.getUsername());
        }

        // Verify that the restaurant exists by restaurantName
        if (!restaurantRepository.existsByRestaurantName(booking.getRestaurantName())) {
            throw new RuntimeException("Restaurant not found with name: " + booking.getRestaurantName());
        }

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByUsername(String username) {
        return bookingRepository.findByUsername(username);
    }

    @Override
    public List<Booking> getBookingsByRestaurantName(String restaurantName) {
        return bookingRepository.findByRestaurantName(restaurantName);
    }
}
