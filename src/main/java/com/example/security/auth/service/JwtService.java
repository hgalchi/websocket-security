package com.example.security.auth.service;

import com.example.security.repository.UserRepository;
import com.example.security.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public String reissueToken(Claims claims) {

        //exprired check
        try{
            jwtUtil.validateClaims(claims);
        }catch (ExpiredJwtException e){
            throw new IllegalAccessError();
        }

        //토큰이 refresh인지 확인
        String id = jwtUtil.getCategory(claims);
        if (!id.equals("refreshToken")) {
            throw new IllegalAccessError();
        }

        String email = jwtUtil.getEmail(claims);
        //UserEntity user = userRepository.findByEmail(email).get();
        List<String> roles = claims.get("roles",List.class);

        //create new accessToken
        return jwtUtil.creteToken(email,roles, "accessToken");
    }


}
