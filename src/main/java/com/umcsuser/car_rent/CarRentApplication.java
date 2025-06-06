package com.umcsuser.car_rent;

import com.umcsuser.car_rent.models.Vehicle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableScheduling
public class CarRentApplication {
	public static void main(String[] args) {
		SpringApplication.run(CarRentApplication.class, args);
	}
}

