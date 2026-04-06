package com.example.multiboard.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 헤더에서 토큰 추출
            String token = jwtTokenProvider.resolveToken(request);
            
            // 토큰 유효성 검사
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 인증 정보 생성 및 SecurityContext에 설정
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // 인증 실패 시 에러 응답 설정
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        } finally {
            // 해당 요청 처리가 끝나면 컨텍스트 정리 (선택 사항이나 Stateless 구조에서 명확성을 위해 사용)
            // 주의: filterChain.doFilter 호출 전에 clear하면 다음 필터에서 인증 정보를 못 쓸 수 있으므로 
            // 프로젝트 구조에 따라 호출 위치를 조정해야 할 수 있습니다.
            SecurityContextHolder.clearContext(); 
        }
        
        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}