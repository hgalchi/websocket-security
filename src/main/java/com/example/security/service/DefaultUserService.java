package com.example.security.service;

import com.example.security.Entity.Group;
import com.example.security.Entity.User;
import com.example.security.dto.UserData;
import com.example.security.repository.GroupRepository;
import com.example.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(User user) {
        if (checkIfUserExist(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        updateCustomerGroup(user);
        userRepository.save(user);
    }

    private void updateCustomerGroup(User userEntity) {
        //하 진짜 개싫다.. 극혐 공백있어서 계속 에러났는데 무슨 에러인지 몰라서 개 쩔쩔헤맷다..
        Group group = groupRepository.findByCode("role_customer").orElseThrow();
        userEntity.addUserGroups(group);
    }

    private boolean checkIfUserExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}
