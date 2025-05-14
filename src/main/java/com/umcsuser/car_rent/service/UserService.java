package com.umcsuser.car_rent.service;

import com.umcsuser.car_rent.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByLogin(String login);
}
