package com.umcsuser.car_rent.service.impl;

import com.umcsuser.car_rent.dto.UserRequest;
import com.umcsuser.car_rent.models.Role;
import com.umcsuser.car_rent.models.User;
import com.umcsuser.car_rent.repository.RoleRepository;
import com.umcsuser.car_rent.repository.UserRepository;
import com.umcsuser.car_rent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(UserRequest req) {
        if (userRepository.findByLogin(req.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Error...");
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() ->
                        new IllegalStateException("There is no role... ROLE_USER"));
        User u = User.builder()
                .id(UUID.randomUUID().toString())
                .login(req.getLogin())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(Set.of(userRole))
                .build();
        userRepository.save(u);
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    @Override
    public List<User> findAll(){
        return userRepository.findAll();
    }
    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
