package com.example.security.controller;

import com.example.security.Entity.UserEntity;
import com.example.security.service.DefaultUserService;
import com.example.security.dto.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.security.utils.JwtUtil;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final DefaultUserService defaultUserService;
    private final JwtUtil jwtUtil;


    @GetMapping("/login")
    public String login() {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("wywudi@naver.com", "password"));
            String email = authentication.getName();
            UserEntity user = UserEntity.builder()
                    .email(email)
                    .build();
            String token = jwtUtil.creteToken(user);
            return "login succ! token :" + token + " /t email : " + email;
        } catch (BadCredentialsException e) {
            return "error";
        }

    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }


    @GetMapping("/register")
    public String register() {
        UserData data = UserData.builder()
                .name("springboot")
                .email("wywudi@kakao.com")
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
