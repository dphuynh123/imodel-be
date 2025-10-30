package com.hat.imodel.controller;

import com.hat.imodel.model.LoginResponse;
import com.hat.imodel.service.GoogleAuthService;
import com.hat.imodel.service.JwtService;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    private final JwtService jwtService;
    private final GoogleAuthService googleAuthService;

    public AuthController(JwtService jwtService, GoogleAuthService googleAuthService) {
        this.jwtService = jwtService;
        this.googleAuthService = googleAuthService;
    }

    @GetMapping("/ping")
    public String test() {
        return "pong";
    }

    @PostMapping("/api/auth/google")
    public Object loginWithGoogle(@RequestBody Map<String, String> body) throws Exception {
        String token = body.get("token");
        var payload = googleAuthService.verifyToken(token);

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        String jwtToken = jwtService.generateToken(new User(name,"", new ArrayList<>()));

        // Create user or generate your own JWT for session
        return LoginResponse.builder().token(jwtToken).expiresIn(jwtService.getExpirationTime()).build();
    }


}
