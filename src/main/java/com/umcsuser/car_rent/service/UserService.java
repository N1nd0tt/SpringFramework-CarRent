package com.umcsuser.car_rent.service;

import com.umcsuser.car_rent.dto.UserRequest;
import com.umcsuser.car_rent.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void register(UserRequest req);
    void softDeleteUser(String userId);
    void removeRole(String userId, String roleName);
    void addRole(String userId, String roleName);
    Optional<User> findByLogin(String login);
    List<User> findAll();
}
