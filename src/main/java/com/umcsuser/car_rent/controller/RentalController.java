package com.umcsuser.car_rent.controller;

import com.umcsuser.car_rent.dto.RentalRequest;
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
    public Rental rentVehicle(@RequestBody RentalRequest rentalDTO) {
        return rentalService.rent(rentalDTO.getVehicleId(), rentalDTO.getUserId());
    }

    @PostMapping("/return")
    public ResponseEntity<Void> returnVehicle(@RequestBody RentalRequest rentalDTO) {
        boolean success = rentalService.returnRental(rentalDTO.getVehicleId(), rentalDTO.getUserId());
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}