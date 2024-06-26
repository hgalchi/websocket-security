package com.example.security.service;

import com.example.security.auth.entity.Group;
import com.example.security.Entity.User;
import com.example.security.codes.ErrorCode;
import com.example.security.exception.BusinessException;
import com.example.security.repository.GroupRepository;
import com.example.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(User user) {
        String email = user.getEmail();
        if (checkIfUserExist(email)) {
            throw new BusinessException(ErrorCode.USER_ALEADY_EXISTS);
        }
        updateCustomerGroup(user);
        userRepository.save(user);
    }

    private void updateCustomerGroup(User userEntity) {
        Group group = groupRepository.findByCode("role_customer").orElseThrow();
        userEntity.addUserGroups(group);
    }

    private boolean checkIfUserExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}
