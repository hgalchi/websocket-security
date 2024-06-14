package com.example.security.controller;

import com.example.security.Entity.Refresh;
import com.example.security.repository.RefreshRepository;
import com.example.security.service.JwtService;
import com.example.security.utils.CookieUtil;
import com.example.security.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
        //refresh token 취득
        Claims claims = jwtUtil.validateToken(refreshToken);

        //만료시간 확인
        try{
            jwtUtil.validateClaims(claims);
        }catch (Exception e){
            return new ResponseEntity<>("refresh token expried", HttpStatus.BAD_REQUEST);
        }

        //토큰이 refresh인지 확인
        String id = jwtUtil.getId(claims);
        if(!id.equals("refresh")) return new ResponseEntity<>("invalid id", HttpStatus.BAD_REQUEST);

        System.out.println(refreshToken);
        //db에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if(!isExist) return new ResponseEntity<>("not exists refreshtoken", HttpStatus.BAD_REQUEST);

        //새로운 jwt 생성
        String email = jwtUtil.getEmail(claims);
        List<String> roles = jwtUtil.getRoles(claims);
        String newRefreshToken =jwtUtil.creteToken(email, roles, "refresh");
        String newAccessToken = jwtUtil.creteToken(email, roles, "access");

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

