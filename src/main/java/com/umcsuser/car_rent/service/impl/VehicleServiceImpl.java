package com.umcsuser.car_rent.service.impl;

import com.umcsuser.car_rent.models.Vehicle;
import com.umcsuser.car_rent.repository.RentalRepository;
import com.umcsuser.car_rent.repository.VehicleRepository;
import com.umcsuser.car_rent.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              RentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;

    }
    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAll () {
        return vehicleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAllActive() {
        return vehicleRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findByIdAndIsActiveTrue(id);
    }

    @Override
    @Transactional
    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        Set<String> rentedVehicleIds = rentalRepository.findRentedVehicleIds();
        return vehicleRepository.findByIsActiveTrueAndIdNotIn(rentedVehicleIds);
    }

    @Override
    @Transactional
    public void updateLocation(String vehicleId, String location) {
        vehicleRepository.findById(vehicleId).ifPresent(vehicle -> {
            vehicle.setLocation(location);
            vehicleRepository.save(vehicle);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findRentedVehicles() {
        Set<String> rentedVehicleIds = rentalRepository.findRentedVehicleIds();
        return vehicleRepository.findAllById(rentedVehicleIds);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAvailable(String vehicleId) {
        return vehicleRepository.findByIdAndIsActiveTrue(vehicleId).isPresent();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        vehicleRepository.findById(id).ifPresent(vehicle -> {
            vehicle.setActive(false);
            vehicleRepository.save(vehicle);
        });
    }
}

