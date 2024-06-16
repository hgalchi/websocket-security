package com.example.security.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * 리소스의 접근권한 인증 서비스
 */
@Service
public class SecurityService  {
    /**
     * 사용자가 리소스의 주인임을 체크
     * @param authentication
     * @param email 리소스를 구분하는 필드
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
