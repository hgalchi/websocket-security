package com.example.security.utils;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageDeliveryException;
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

//todo : 외부설정 파일에서 정보 가져오기
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
     * 토큰 생성
     * @param email 사용자 이메일
     * @param roles 사용자 권한
     * @param catetory  accesstoken,refreshtoken을 구분하기 위한 문자열
     * @return 생성된 JWT토큰
     */
    public String creteToken(String email,List<String> roles,String catetory) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        claims.put("catetory", catetory);

        Long token=(Objects.equals(catetory, "accessToken") ? Long.parseLong(accessExpirationTime) : Long.parseLong(refreshExpirationTime));
        Date tokenValidity = new Date(new Date().getTime() + TimeUnit.MINUTES.toMillis(token));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    /**
     * refresh만료 기간 반환
     */
    public String refreshExpireTime() {
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(Long.parseLong(refreshExpirationTime)));
        return tokenValidity.toString();
    }


    /**
     * JWT 토큰을  Claims으로 변환
     * @return 토큰에서 추출한 Claims
     */
    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    /**
     * JWT 토큰의 유효성 검증
     * @throws  SignatureException,MalformedJwtException,ExpiredJwtException 토큰이 유효하지 않을 때 발생
     * @param token
     * @return
     */
    public Claims validateToken(String token) {

        try {
            if (token != null) return parseJwtClaims(token);
            return null;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Http 요청쿠키에서 refresh 토큰 추출
     */
    public String parseRefresh(HttpServletRequest req) {
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
     * Bearer 토큰에서 JWT 토큰 분리
     */
    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }return null;
    }

    /**
     * Claims의 만료 기간 검증
     * @return 만료기간이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateClaims(Claims claims) {
            return claims.getExpiration().after(new Date());
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
