package com.example.multiboard.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.multiboard.member.model.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    // HS256 알고리즘을 위한 시크릿 키 생성
    private static final SecretKey key = Jwts.SIG.HS256.key().build();

    private static final String AUTH_HEADER = "X-AUTH-TOKEN";
    private long tokenValidTime = 30 * 60 * 1000L; // 토큰 유효시간 30분

    @Autowired
    UserDetailsService userDetailsService;

    // JWT 토큰 생성
    public String generateToken(Member member) {
        long now = System.currentTimeMillis();
        
        Claims claims = Jwts.claims()
                .subject(member.getUserid())     // sub
                .issuer(member.getName())        // iss
                .issuedAt(new Date(now))         // iat
                .expiration(new Date(now + tokenValidTime)) // exp
                .add("roles", member.getRole())  // roles 추가
                .build();

        return Jwts.builder()
                .claims(claims)
                .signWith(key) // 암호화에 사용할 키 설정
                .compact();
    }

    // HTTP Request 헤더에서 토큰 값 추출
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

    // 토큰의 유효성 검사 및 클레임 추출
    private Claims parseClaims(String token) {
        log.info("Parsing token: {}", token);
        return Jwts.parser()
                .verifyWith(key) // 키를 이용하여 토큰 검증
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰에서 사용자 아이디 추출
    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }

    // JWT 토큰으로 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserId(token));
        log.info("Authenticated user: {}", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰의 유효성 및 만료일자 확인
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}