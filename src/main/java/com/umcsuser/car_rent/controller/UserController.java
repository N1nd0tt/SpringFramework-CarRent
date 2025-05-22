package com.umcsuser.car_rent.controller;

import com.umcsuser.car_rent.models.User;
import com.umcsuser.car_rent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> softDeleteUser(@PathVariable String id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok("User soft-deleted successfully.");
    }

    @GetMapping
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            List<User> allUsers = userService.findAll();
            return ResponseEntity.ok(allUsers);
        } else {
            String username = authentication.getName();
            User user = userService.findByLogin(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
            return ResponseEntity.ok(user);
        }
    }

    @PutMapping("/admin/{id}/remove-role")
    public ResponseEntity<String> removeRoleFromUser(@PathVariable String id, @RequestParam String role) {
        userService.removeRole(id, role);
        return ResponseEntity.ok("Role removed successfully.");
    }

    @PutMapping("/admin/{id}/add-role")
    public ResponseEntity<String> addRoleToUser(@PathVariable String id, @RequestParam String role) {
        userService.addRole(id, role);
        return ResponseEntity.ok("Role added successfully.");
    }
}
