package com.example.security.controller;

import com.example.security.config.service.DefaultUserService;
import com.example.security.dto.UserData;
import com.example.security.config.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final DefaultUserService defaultUserService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/loginUser")
    public String login() {
       Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated("wywudi@kakao.com", "password");
       authenticationManager.authenticate(authentication);

        return "loginUser";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/save")
    public String authenticationProviders() {

        userService.saveUser(UserData.builder().name("springboot").email("wywudi@naver.com").password("password").build());

        return "test1";

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


    @GetMapping("/account/hi")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String accountHi() {

        return "accountHi";
    }

    @GetMapping("/endpoint")
    @PreAuthorize("hasRole(ADMIN)")
    public String endpoint() {

        return "endPoint";
    }

    @GetMapping("/resource/authentication")
    public String authentication() {
        return "authentication";
    }

    @GetMapping("/any")
    public void authoriyRead(){

    }

    @PostMapping("/any")
    public void authoriyWriter(){

    }
}
