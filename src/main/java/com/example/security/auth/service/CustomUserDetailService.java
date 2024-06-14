package com.example.security.auth.service;

import com.example.security.Entity.Group;
import com.example.security.Entity.User;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * email로 UserDetails 객체를 반환
     * @param email 사용자의 이메일
     * @throws UsernameNotFoundException : 이메일로 등록된 사용자가 없을 경우
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User customer = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found"+email));

        UserDetails user = org.springframework.security.core.userdetails.User.withUsername(customer.getEmail())
                .password(customer.getPassword())
                .authorities(getAuthorities(customer))
                .build();

        return user;
    }

    /**
     *사용자 권한을 반환
     * @param user 사용자 객체
     * @return Collection<GrantedAuthority> 사용자 권한 리스트
     */
    private Collection<GrantedAuthority> getAuthorities(User user) {
        Set<Group> userGroups = user.getUserGroups();
        Collection<GrantedAuthority> authorities = new ArrayList<>(userGroups.size());
        for (Group group : userGroups) {
            authorities.add(new SimpleGrantedAuthority(group.getCode().toUpperCase()));
        }
        return authorities;
    }
}