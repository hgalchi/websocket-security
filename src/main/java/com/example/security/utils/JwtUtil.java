package com.example.security.utils;

import com.example.security.Entity.UserEntity;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtUtil {


    @Value("${refresh.expriation-time}")
    private String refreshExpirationTime;

    @Value("${access.expriation-time}")
    private String accessExpirationTime;

    @Value("${jwt.secret-key}")
    private String secret_key;

    private final String TOKEN_HADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer";


    private final JwtParser jwtParser;

    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey("mysecretkey");
    }

    /**
     * 
     * @param email
     * @param roles
     * @param id
     * @return
     */
    public String creteToken(String email,List<String> roles,String id) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        claims.put("id", id);

        Long token=(Objects.equals(id, "accessToken") ? Long.parseLong(accessExpirationTime) : Long.parseLong(refreshExpirationTime));
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(token));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    public String refreshExpireTime() {
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(Long.parseLong(refreshExpirationTime)));
        return tokenValidity.toString();
    }


    /**
     * Claims로 변환
     * @param token
     * @return
     */
    public Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    /**
     * token의 valid검사
     * @param req
     * @return
     */
    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) return parseJwtClaims(token);
            return null;
        } catch (ExpiredJwtException e) {
            req.setAttribute("expired", e.getMessage());
            throw e;
        } catch (Exception e) {
            req.setAttribute("invalid", e.getMessage());
            throw e;
        }
    }

    public String resolveCookie(HttpServletRequest req) {
        //get refresh token
        String refresh = null;
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }
        return refresh;

    }


    /**
     * req로 전송된 토큰 분리
     * @param req
     * @return
     */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(TOKEN_HADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }return null;
    }

    /**
     * valid token exprire
     * @param claims
     * @return
     */
    public boolean validateClaims(Claims claims) {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    public List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }

    public String getId(Claims claims) {
        return claims.get("id").toString();
    }


}
