package com.example.security.service;

import com.example.security.Entity.Group;
import com.example.security.Entity.UserEntity;
import com.example.security.dto.UserData;
import com.example.security.repository.GroupRepository;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(UserData user) {
        if (checkIfUserExist(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        UserEntity userEntity = user.toUserEntity();
        updateCustomerGroup(userEntity);
        userRepository.save(userEntity);
    }

    private void updateCustomerGroup(UserEntity userEntity) {
        Group group = groupRepository.findByCode("role_customer");
        userEntity.addUserGroups(group);
    }

    private boolean checkIfUserExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
