package com.example.security.auth.handler;

import com.example.security.auth.entity.Refresh;
import com.example.security.Entity.User;
import com.example.security.repository.RefreshRepository;
import com.example.security.repository.UserRepository;
import com.example.security.utils.CookieUtil;
import com.example.security.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final CookieUtil cookieUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            handle(response, authentication);
            clearAuthenticationAttributes(request);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RefreshToken과 AccessToken을 생성해 응답 객체의 헤더와 쿠키에 전달
     * @param response
     * @param authentication
     * @throws IllegalAccessException
     */
    protected void handle(HttpServletResponse response, Authentication authentication) throws IllegalAccessException {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).get();
        List<String> authorities =  authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String accessToken = jwtUtil.creteToken(email,authorities, "accessToken");
        String refreshToken = jwtUtil.creteToken(email,authorities, "refreshToken");

        //AccessToken 재발행을 위해 서버에 저장
        String expiration = jwtUtil.refreshExpireTime();
        Refresh refresh = Refresh.builder()
                .expiration(expiration)
                .refresh(refreshToken)
                .user(user)
                .build();
        refreshRepository.save(refresh);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(cookieUtil.createCookie("refresh", refreshToken));
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    //권한에 따른 redirect 페이지
   /* protected String determineTargetUrl(Authentication authentication) throws IllegalAccessException {

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
    }*/

}
