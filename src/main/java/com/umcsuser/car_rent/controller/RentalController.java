package com.umcsuser.car_rent.controller;

import com.umcsuser.car_rent.models.Rental;
import com.umcsuser.car_rent.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public List<Rental> getAllRentals() {
        return rentalService.findAll();
    }

    @PostMapping("/rent")
    public Rental rentVehicle(@RequestParam String vehicleId, @RequestParam String userId) {
        return rentalService.rent(vehicleId, userId);
    }

    @PostMapping("/return")
    public ResponseEntity<Void> returnVehicle(@RequestParam String vehicleId, @RequestParam String userId) {
        boolean success = rentalService.returnRental(vehicleId, userId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}