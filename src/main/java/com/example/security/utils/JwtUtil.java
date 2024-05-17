package com.example.security.utils;

import com.example.security.Entity.UserEntity;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final String secret_key = "mysecretkey";
    private long accessTokenValidity = 60 * 60 * 1000;

    private final JwtParser jwtParser;

    private final String TOKEN_HADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer";

    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }

    /**
     * jwt token생성
     * @param user
     * @return
     */
    public String creteToken(UserEntity user) {

        List<String> roles = user.getUserGroups().stream()
                .map(t ->t.getCode().toUpperCase())
                .collect(Collectors.toList());

        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("roles", roles);
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    private String refreshToken(String token) {

    }


    /**
     * Claims로 변환
     * @param token
     * @return
     */
    private Claims parseJwtClaims(String token) {
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

    //왜 private
    public List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }
}
