package com.example.security.controller;

import com.example.security.Entity.UserEntity;
import com.example.security.repository.UserRepository;
import com.example.security.service.DefaultUserService;
import com.example.security.dto.UserData;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.security.utils.JwtUtil;

import java.io.IOException;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;

    private final DefaultUserService defaultUserService;

    @GetMapping("/home")
    public String home(HttpServletRequest request) {
        System.out.println("home 리디렉션");
        return "home";
    }

    @GetMapping("/register")
    public String register() {
        UserData data = UserData.builder()
                .name("springboot")
                .email("wywudi@naver.com")
                .password(passwordEncoder.encode("password"))
                .build();

        defaultUserService.register(data);

        return "register";
    }

    @GetMapping("/customer")
    public String accountHi() {
        return "authorities customer";
    }

    @GetMapping("/admin")
    public String endpoint() {
        return "admin";
    }


    @GetMapping("/any")
    public String any() {
        return "any";
    }
}
