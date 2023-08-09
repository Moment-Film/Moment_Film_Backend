package com.team_7.moment_film.global.filter;

import com.team_7.moment_film.global.security.UserDetailsServiceImpl;
import com.team_7.moment_film.global.util.JwtUtil;
import com.team_7.moment_film.global.util.RedisUtil;
import io.jsonwebtoken.Claims;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Slf4j(topic = "JWT 검증 및 인가 필터")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = req.getHeader("accessToken");

        if (StringUtils.hasText(accessToken)) {
            accessToken = jwtUtil.substringToken(accessToken);

            // accessToken 검증 실패 시 refreshToken 을 통해 accessToken 재발급 진행
            if (!jwtUtil.validateToken(accessToken)) {
                log.warn("Token Error, accessToken 검증 실패! refreshToken 으로 JWT 재발급 시도");
                String refreshToken = jwtUtil.getRefreshToken(req.getHeader("refreshToken"));

                if (StringUtils.hasText(refreshToken) && jwtUtil.validateToken(refreshToken)) {
                    // 최초 로그인 시 redis 에 저장한 refreshToken 을 대조
                    Claims userInfo = jwtUtil.getUserInfoFromToken(refreshToken);
                    log.info("제출한 refresh 토큰 검증 성공, refreshToken 으로 Claims 조회");

                    Long userId = Long.valueOf(userInfo.getSubject());
                    String email = userInfo.get("email").toString();
                    String username = userInfo.get("username").toString();

                    String redisRefreshToken = redisUtil.getData(username);
                    log.info("redis 에서 기존에 저장된 refreshToken 불러오기 성공");

                    // redis 에서 가져온 refreshToken 이 유효하고, 제출한 refreshToken 과 일치한 경우 새 accessToken, refreshToken 재발급
                    if (StringUtils.hasText(redisRefreshToken) && refreshToken.equals(redisRefreshToken)) {
                        String accessTokenWithBearer = jwtUtil.createAccessToken(userId, username, email);
                        accessToken = jwtUtil.substringToken(accessTokenWithBearer);
                        refreshToken = jwtUtil.createRefreshToken(userId, username, email);
                        log.info("accessToken, refreshToken 재발급 성공");

                        String refreshTokenValue = jwtUtil.substringToken(refreshToken);
                        Date expiration = jwtUtil.getUserInfoFromToken(refreshTokenValue).getExpiration();

                        // redis 에 refreshToken 값 덮어쓰기
                        redisUtil.setData(username, refreshTokenValue, expiration);
                        log.info("redis에 새로 발급한 refreshToken 저장");

                        res.setHeader("accessToken", accessTokenWithBearer);
                        res.setHeader("refreshToken", refreshToken);
                        log.info("헤더에 새로 발급한 ACT, RFT 저장");
                    }
                } else {
                    // accessToken 검증 실패 후 refreshToken의 형식이 올바르지 않거나, 만료되었을 경우
                    log.error("올바르지 않은 Token 형식이거나, refreshToken 만료됨. 다시 로그인을 해주세요.");
                    return;
                }
            }

            try {
                Claims info = jwtUtil.getUserInfoFromToken(accessToken);
                log.info("Create Authentication, 인증 객체 생성 및 SecurityContextHolder 저장 성공");
                setAuthentication((String) info.get("email"));
            } catch (Exception e) {
                // 인증 객체를 생성할 수 없는 경우
                log.error("Failed to create Authentication : " + e.getMessage());
                return;
            }
        }
        // Authentication 필터로 이동
        filterChain.doFilter(req, res);
    }

    // SecurityContextHolder 에 SecurityContext 및 Authentication 세팅
    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // Authentication(인증 객체) 생성하는 메서드
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
