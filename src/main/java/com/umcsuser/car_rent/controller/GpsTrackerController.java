package com.umcsuser.car_rent.controller;

import com.umcsuser.car_rent.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gps")
public class GpsTrackerController {
    private final VehicleService vehicleService;

    @Autowired
    public GpsTrackerController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/set-location/{vehicleId}")
    public String setLocation(@PathVariable String vehicleId, @RequestParam String location){
        vehicleService.updateLocation(vehicleId, location);
        return "Location updated successfully to " + location + " for vehicle ID: " + vehicleId;
    }
}
