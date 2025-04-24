package com.diner.backend.controller;

import com.diner.backend.entity.Booking;
import com.diner.backend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/user/{username}")
    public List<Booking> getBookingsByUsername(@PathVariable String username) {
        return bookingService.getBookingsByUsername(username);
    }

    @GetMapping("/restaurant/{restaurantName}")
    public List<Booking> getBookingsByRestaurantName(@PathVariable String restaurantName) {
        return bookingService.getBookingsByRestaurantName(restaurantName);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestBody Booking booking) {
        System.out.println(booking.toString());
        return bookingService.createBooking(booking);
    }
//
//    @PutMapping("/{bookingId}/cancel")
//    public Booking cancelBooking(@PathVariable Long bookingId) {
//        return bookingService.cancelBooking(bookingId); // This should set status to CANCELLED
//    }

}
