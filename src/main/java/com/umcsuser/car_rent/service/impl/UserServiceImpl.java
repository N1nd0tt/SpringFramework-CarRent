package com.umcsuser.car_rent.service.impl;

import com.umcsuser.car_rent.models.User;
import com.umcsuser.car_rent.repository.UserRepository;
import com.umcsuser.car_rent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
