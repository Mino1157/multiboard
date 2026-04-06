package com.example.multiboard.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.multiboard.jwt.JwtTokenProvider;
import com.example.multiboard.member.model.Member;
import com.example.multiboard.member.service.IMemberService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MemberRestController {

    @Autowired
    private IMemberService memberService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // JWT 기반 로그인 처리
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) {
        log.info("Login attempt for user: {}", user.get("userid"));
        
        Member member = memberService.selectMember(user.get("userid"));
        if (member == null) {
            throw new IllegalArgumentException("사용자가 없습니다.");
        }
        
        // 암호화된 비밀번호와 입력된 비밀번호 비교
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        
        // 인증 성공 시 JWT 토큰 생성 및 반환
        return jwtTokenProvider.generateToken(member);
    }

    // JWT 토큰 테스트용 엔드포인트
    @GetMapping("/test_jwt")
    public String testJwt(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        log.info("Token received: {}", token);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            log.info("Principal: {}, Name: {}, Authorities: {}", 
                     auth.getPrincipal(), auth.getName(), auth.getAuthorities());
            log.info("Is Token Valid: {}", jwtTokenProvider.validateToken(token));
            
            return jwtTokenProvider.getUserId(token);
        } else {
            return "Invalid or missing token";
        }
    }
}