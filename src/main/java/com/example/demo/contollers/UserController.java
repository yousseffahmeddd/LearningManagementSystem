package com.example.demo.contollers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.example.demo.models.AuthRequest;
import com.example.demo.models.User;
import com.example.demo.service.JWTService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService service;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    UserController(UserService service, JWTService jwtService, AuthenticationManager authenticationManager) {
        this.service = service;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> addNewUser(@RequestBody User userInfo) {
        String response = service.addUser(userInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/generateToken")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(token);
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

}