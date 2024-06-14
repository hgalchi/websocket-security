package com.example.security.controller;

import com.example.security.Entity.User;
import com.example.security.repository.UserRepository;
import com.example.security.service.DefaultUserService;
import com.example.security.dto.UserData;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.security.utils.JwtUtil;

import java.io.IOException;
import java.security.Principal;
import java.security.SignatureException;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final DefaultUserService defaultUserService;


    @PostMapping("/register")
    public String register(@Validated @RequestBody UserData dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        User user = dto.toUserEntity();

        defaultUserService.register(user);

        return "register succ";
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

    @GetMapping("/customError")
    public String customError() {
        return "404 페이지 접근 권한이 없습니다.";
    }

    @GetMapping("/login")
    public String loginError(@RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "exception", required = false) String exception) {
        return "error: "+error+", exception: "+exception;
    }
    //meta annotion으로 따로 만들어줘도 사용가능
    //메서드의 파라미터 값에 따라 반환되는 권한이 다름으로
    //반환값과 현재 사용자의 정보를 비교해야하므로 post
    @PostAuthorize("isAuthenticated() and(returnObject.email==authentication.name)")
    @GetMapping("/postTest/{seq}")
    public UserData getEmail(@PathVariable("seq") long seq, Principal principal) {

        User user = userRepository.findById(seq).orElse(null);
        UserData reDto = UserData.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
        return reDto;
    }

    @GetMapping("/preTest/{email}")
    @PreAuthorize("@mySecurityService.isResourceOwner(authentication,#email)")
    //@PreAuthorize("isAuthenticated() and #email==authentication.name")
    public String getName(@PathVariable("email") String email) {
        System.out.println("이메일: " + email);
        return "접근허가";
    }


}
