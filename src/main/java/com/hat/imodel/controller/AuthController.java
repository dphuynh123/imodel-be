package com.hat.imodel.controller;

import com.hat.imodel.entity.
        User;
import com.hat.imodel.model.LoginResponse;
import com.hat.imodel.repository.UserRepository;
import com.hat.imodel.service.GoogleAuthService;
import com.hat.imodel.service.JwtService;

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
    private final UserRepository userRepository;

    public AuthController(JwtService jwtService, GoogleAuthService googleAuthService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.googleAuthService = googleAuthService;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/auth/google")
    public Object loginWithGoogle(@RequestBody Map<String, String> body) throws Exception {
        String token = body.get("token");
        var payload = googleAuthService.verifyToken(token);

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = User.builder().email(email).username(name).point(0).passwordHash("changeit").build();
            userRepository.save(user);
        }
        String jwtToken = jwtService.generateToken(user);

        
        return LoginResponse.builder().token(jwtToken).expiresIn(jwtService.getExpirationTime()).build();
    }


}
