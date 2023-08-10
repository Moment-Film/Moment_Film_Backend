package com.team_7.moment_film.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team_7.moment_film.domain.user.dto.LoginRequestDto;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import com.team_7.moment_film.global.util.JwtUtil;
import com.team_7.moment_film.global.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

@Slf4j(topic = "JWT 생성 및 인증 필터")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;


    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisUtil redisUtil) {
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;

        setFilterProcessesUrl("/api/user/login");
    }

    // 사용자의 로그인 정보를 받아 Authentication(인증 객체)를 반환하는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            // 사용자 전송 data DTO 객체로 Mapping
            LoginRequestDto requestDto = new ObjectMapper().readValue(req.getInputStream(), LoginRequestDto.class);

            // UsernamePasswordAuthenticationToken 생성
            // authenticate UsernamePasswordAuthenticationToken 을 담음
            // AuthenticationManager 에 authenticate 를 담아 인증 객체인 Authentication 반환
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error("Authentication 인증 객체 생성 실패", e.getMessage());
            throw new IllegalArgumentException("요청한 데이터를 parsing 할 수 없습니다.");
        }
    }

    // Authentication(인증 객체)를 받아 JWT 생성 후 로그인 처리하는 메서드
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authentication) throws IOException, SecurityException {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        String username = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        String email = ((UserDetailsImpl) authentication.getPrincipal()).getUser().getEmail();

        String accessToken = jwtUtil.createAccessToken(userId, username, email);
        String refreshToken = jwtUtil.createRefreshToken(userId, username, email);
        log.info("로그인 성공 및 JWT 생성");


        // redis에 refreshToken 저장
        String refreshTokenValue = jwtUtil.substringToken(refreshToken);
        Date expiration = jwtUtil.getUserInfoFromToken(refreshTokenValue).getExpiration();
        redisUtil.setData(username, refreshTokenValue, expiration);
        log.info("Redis에 RefreshToken 저장");

        // response Header
        res.setHeader("accessToken", accessToken);
        res.setHeader("refreshToken", refreshToken);
        // response body
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("로그인 성공");
    }

    // Authentication(인증 객체)의 Exception 이 발생한 경우 로그인 실패 처리 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException authenticationException) throws IOException, SecurityException {
        log.info("로그인 실패");
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write("로그인 실패");
    }
}
