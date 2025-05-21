package com.umcsuser.car_rent.controller;

import com.umcsuser.car_rent.models.User;
import com.umcsuser.car_rent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

//    @GetMapping
//    public User getCurrentUserInfo(){
//        return userService.findAll();
//    }

    @GetMapping
    public List<User> getAllUsersInfo(){
        return userService.findAll();
    }
}
