package com.example.security.config.service;

import com.example.security.Entity.Group;
import com.example.security.Entity.UserEntity;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email+"not found"));

        UserDetails user = User.withUsername(customer.getEmail())
                .password(customer.getPassword())
                .authorities(getAuthorities(customer))
                .build();

        return user;
    }

    private Collection<GrantedAuthority> getAuthorities(UserEntity user) {
        Set<Group> userGroups = user.getUserGroups();
        Collection<GrantedAuthority> authorities = new ArrayList<>(userGroups.size());
        for (Group group : userGroups) {
            authorities.add(new SimpleGrantedAuthority(group.getCode().toUpperCase()));
        }
        return authorities;
    }
}
