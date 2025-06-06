package com.umcsuser.car_rent.scheduler;

import com.umcsuser.car_rent.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Component
public class VehicleLocationScheduler {
    private final VehicleRepository vehicleRepository;
    private final Random random = new Random();

    @Autowired
    public VehicleLocationScheduler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateVehicleLocations() {
        List<String> locations = List.of("Company HQ", "Service Center", "Customer Location");
        vehicleRepository.findAll().forEach(vehicle -> {
            vehicle.setLocation(locations.get(random.nextInt(locations.size())));
            vehicleRepository.save(vehicle);
        });
    }
}
