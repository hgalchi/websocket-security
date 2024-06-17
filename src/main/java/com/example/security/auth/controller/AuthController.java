package com.example.security.auth.controller;

import com.example.security.Entity.User;
import com.example.security.repository.UserRepository;
import com.example.security.dto.UserData;
import com.example.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService defaultUserService;


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


    @PostAuthorize("isAuthenticated() and(returnObject.email==authentication.name)")
    @GetMapping("/boardPost/{seq}")
    public UserData getEmail(@PathVariable("seq") long seq, Principal principal) {

        User user = userRepository.findById(seq).orElse(null);
        UserData reDto = UserData.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
        return reDto;
    }

  //todo : 게시글 등록 시 email을 id로 변경
    @GetMapping("/boardRegister/{email}")
    @PreAuthorize("@mySecurityService.isResourceOwner(authentication,#email)")
    //@PreAuthorize("isAuthenticated() and #email==authentication.name")
    public String getName(@PathVariable("email") String email) {
        System.out.println("이메일: " + email);
        return "접근허가";
    }


    @GetMapping("/customError")
    public String customError() {
        return "404 페이지 접근 권한이 없습니다.";
    }

    @GetMapping("/login")
    public String loginError(@RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "exceptionMessage", required = false) String exceptionMessage) {
        return "error: " + error + ", exception: " + exceptionMessage;
    }
}
