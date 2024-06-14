package com.example.security.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * 리소스에 접근권한 인증
 */
@Service
public class MySecurityService  {
    /**
     * 리소스의 주인이 사용자인가 인증
     * @param authentication
     * @param email 리소스의 분별가능한
     * @return
     */
    public boolean isResourceOwner(Authentication authentication, String email) {
        System.out.println("authentication = " + authentication.getName());
        System.out.println("사용자 = " + email);
        return authentication.getName().equals(email);
    }

    public boolean workCheck() {
        System.out.println("mysecurityService workCheck 확인");
        return true;
    }
}
