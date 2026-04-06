package com.example.multiboard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.multiboard.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 다양한 암호화 방식을 지원하는 기본 인코더 설정
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화 (Stateless 구조이므로)
        http.csrf((csrf) -> csrf.disable());

        // 요청 권한 설정
        http.authorizeHttpRequests((authHttpReq) -> authHttpReq
            .requestMatchers("/file/**").hasRole("ADMIN")
            .requestMatchers("/board/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/member/insert", "/member/login").permitAll()
            .requestMatchers("/**").permitAll());

        // Session 기반의 인증을 사용하지 않고 JWT를 이용하기 위해 STATELESS 설정
        http.sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Spring Security 필터 체인에 JWT 필터 추가 (UsernamePasswordAuthenticationFilter 이전)
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}