package com.example.security.config.service;

import com.example.security.Entity.Refresh;
import com.example.security.Entity.UserEntity;
import com.example.security.repository.RefreshRepository;
import com.example.security.repository.UserRepository;
import com.example.security.utils.CookieUtil;
import com.example.security.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.Media;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {



    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("사용자 인증 성공");

        try {
            handle(request, response, authentication);
            clearAuthenticationAttributes(request);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException, IllegalAccessException {

        String targetUrl = determineTargetUrl(authentication);

        String email = authentication.getPrincipal().toString();
        List<String> authorities =  authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        //todo : token생성 시 만료기간과 메서드 호출로 생성된 만료기간이 같다고 볼 수 있나
        //token 생성
        String accessToken = jwtUtil.creteToken(email,authorities, "accessToken");
        String refreshToken = jwtUtil.creteToken(email,authorities, "refreshToken");

        //Refresh token 저장
        String expireation = jwtUtil.refreshExpireTime();
        Refresh refresh = Refresh.builder()
                .expiration(expireation)
                .refresh(refreshToken)
                .build();

        refreshRepository.save(refresh);

        if (response.isCommitted()) {
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(cookieUtil.createCookie("refresh", refreshToken));

       // redirectStrategy.sendRedirect(request, response, "/home");

        System.out.println("access의 값은"+response.getHeader("Authorization"));


    }

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    protected String determineTargetUrl(Authentication authentication) throws IllegalAccessException {

        Map<String, String> roleTargetUrlMap = new HashMap<String, String>();
        roleTargetUrlMap.put("ROLE_ADMIN", "/admin");
        roleTargetUrlMap.put("ROLE_CUSTOMER", "/customerHome");

        Collection<? extends GrantedAuthority> authorities =
                authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (roleTargetUrlMap.containsKey(authorityName)) {
                return roleTargetUrlMap.get(authorityName);
            }
        }
        throw new IllegalAccessException();
    }

}
