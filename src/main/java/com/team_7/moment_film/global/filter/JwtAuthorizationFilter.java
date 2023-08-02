package com.team_7.moment_film.global.filter;

import com.team_7.moment_film.global.jwt.JwtUtil;
import com.team_7.moment_film.global.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가 필터")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // HttpServletRequest 에서 accessToken 가져온 후 Bearer 를 제거한 토큰 값 불러옴
        String accessToken = jwtUtil.substringToken(req.getHeader("accessToken"));

        if (!jwtUtil.validateToken(accessToken)) {
            log.error("토큰 검증 실패");
            return;
        }

        // accessToken 에서 Subject(Id값) 가져온 후 Long 타입으로 변환
        Long UserId = Long.parseLong(jwtUtil.getUserInfo(accessToken).getSubject());

        try {
            setAuthentication(UserId);
        } catch (Exception e) {
            log.info("비로그인 사용자");
        }
        filterChain.doFilter(req, res);
    }

    // SecurityContextHolder 에 SecurityContext 및 Authentication 세팅
    public void setAuthentication(Long userId) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(userId);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // Authentication(인증 객체) 생성하는 메서드
    private Authentication createAuthentication(Long userId) {
        UserDetails userDetails = userDetailsService.loadUserById(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
