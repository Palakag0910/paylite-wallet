package com.paylite.wallet.controller;

import com.paylite.wallet.dto.LoginRequest;
import com.paylite.wallet.dto.MeResponse;
import com.paylite.wallet.dto.SignupRequest;
import com.paylite.wallet.dto.TokenResponse;
import com.paylite.wallet.dto.UserResponse;
import com.paylite.wallet.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@Valid @RequestBody SignupRequest request)
    {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request)
    {
        return authService.login(request);
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication)
    {
        return authService.me((Long) authentication.getPrincipal());
    }
}
