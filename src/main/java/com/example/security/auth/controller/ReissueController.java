package com.example.security.auth.controller;

import com.example.security.auth.entity.Refresh;
import com.example.security.codes.ErrorCode;
import com.example.security.exception.BusinessException;
import com.example.security.repository.RefreshRepository;
import com.example.security.auth.service.JwtService;
import com.example.security.utils.CookieUtil;
import com.example.security.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final CookieUtil cookieUtil;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest req, HttpServletResponse res) {

        String refreshToken = jwtUtil.parseRefresh(req);
        jwtUtil.validateToken(refreshToken);

        Claims claims = jwtUtil.validateToken(refreshToken);
        jwtUtil.validateClaims(claims);

        //refresh Token 확인
        String id = jwtUtil.getCategory(claims);
        if(!id.equals("refreshToken")) throw new BusinessException(ErrorCode.CATEGORY_NOT_REFRESH);

        //db에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if(!isExist) throw new BusinessException(ErrorCode.TOKEN_NOT_EXIST);

        //새로운 jwt 생성
        String email = jwtUtil.getEmail(claims);
        List<String> roles = jwtUtil.getRoles(claims);
        String newRefreshToken =jwtUtil.creteToken(email, roles, "refreshToken");
        String newAccessToken = jwtUtil.creteToken(email, roles, "accessToken");

        Refresh newRefresh = Refresh.builder()
                .refresh(newRefreshToken)
                .expiration(jwtUtil.refreshExpireTime())
                .build();

        //기존 refreshToken삭제와 새로운 refreshToken저장
        refreshRepository.deleteByRefresh(refreshToken);
        refreshRepository.save(newRefresh);

        res.setHeader("access", newAccessToken);
        res.addCookie(cookieUtil.createCookie("refresh", newRefreshToken));

        return new ResponseEntity<>(HttpStatus.OK);

    }

}

