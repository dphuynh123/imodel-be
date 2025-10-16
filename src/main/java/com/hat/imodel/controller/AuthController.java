package com.hat.imodel.controller;

import com.hat.imodel.model.LoginResponse;
import com.hat.imodel.service.JwtService;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping(path = "/login_default")
    public String login(){

        return "hello";
    }

    @PostMapping("/auth/login")
    public LoginResponse authenticate() {

        String jwtToken = jwtService.generateToken(new User("hello","123", List.of(() -> "USER")));
        LoginResponse loginResponse = LoginResponse.builder().token(jwtToken).expiresIn(jwtService.getExpirationTime()).build();
        return loginResponse;
    }

    @GetMapping("/ping")
    public String test() {

        return "pong";
    }


}
