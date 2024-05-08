package com.example.security.service;

import com.example.security.Entity.UserEntity;
import com.example.security.dto.UserData;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity saveUser(UserData userData) {
        return userRepository.save(dtoToEntity(userData));
    }

    public UserEntity dtoToEntity(UserData userData) {
        return UserEntity.builder()
                .name(userData.getName())
                .email(userData.getEmail())
                .password(passwordEncoder.encode(userData.getPassword()))
                .build();
    }
}
