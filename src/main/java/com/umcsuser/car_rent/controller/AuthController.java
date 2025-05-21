package com.umcsuser.car_rent.controller;

import com.umcsuser.car_rent.dto.UserRequest;
import com.umcsuser.car_rent.dto.UserResponse;
import com.umcsuser.car_rent.security.JwtUtil;
import com.umcsuser.car_rent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest userRequest) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequest.getLogin(),
                            userRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        UserResponse responseBody = new UserResponse(token);
        return ResponseEntity.ok(responseBody);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest req) {
        try {
            userService.register(req);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Registered successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }
}
