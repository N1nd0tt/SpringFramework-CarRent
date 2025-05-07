package com.umcsuser.car_rent.service.impl;

import com.umcsuser.car_rent.models.Rental;
import com.umcsuser.car_rent.models.User;
import com.umcsuser.car_rent.models.Vehicle;
import com.umcsuser.car_rent.repository.RentalRepository;
import com.umcsuser.car_rent.repository.UserRepository;
import com.umcsuser.car_rent.repository.VehicleRepository;
import com.umcsuser.car_rent.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository, VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.existsByVehicleIdAndReturnDateIsNull(vehicleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findActiveRentalByVehicleId(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
    }

    @Override
    @Transactional
    public Rental rent(String vehicleId, String userId) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
        Optional<User> user = userRepository.findById(userId);
        if (vehicle.isEmpty() || user.isEmpty()) {
            throw new IllegalArgumentException("Vehicle or User not found");
        }
        Rental rental = Rental.builder()
                        .id(UUID.randomUUID().toString())
                        .user(user.get())
                        .vehicle(vehicle.get())
                        .rentDate(java.time.LocalDate.now().toString())
                        .build();
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public boolean returnRental(String vehicleId, String userId) {
        Optional<Rental> rental = rentalRepository.findByVehicleIdAndUserIdAndReturnDateIsNull(vehicleId, userId);
        if (rental.isPresent()) {
            rental.get().setReturnDate(java.time.LocalDate.now().toString());
            rentalRepository.save(rental.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }
}