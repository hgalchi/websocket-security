package com.example.security.controller;

import com.example.security.dto.UserData;
import com.example.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginContoller {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;


    @GetMapping("/loginUser")
    public String login() {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated("wywudi@naver.com", "password");
        Authentication authenticationRespones =
                authenticationManager.authenticate(authentication);

        return "loginUser: " + authenticationRespones.getName();
    }

    @GetMapping("/endPoint")
    public String endpoint() {
        return "endPoint";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/save")
    public String authenticationProviders() {

        userService.saveUser(new UserData("name1", "password", "wywudi@naver.com"));

        return "test1";

    }
}
