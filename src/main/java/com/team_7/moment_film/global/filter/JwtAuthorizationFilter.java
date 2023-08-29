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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가 필터")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = req.getHeader("accessToken");

        // accessToken 토큰 값이 없을 경우 비로그인 사용자, 다음 필터(Authentication)로 이동
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer")) {
            accessToken = jwtUtil.substringToken(accessToken);

            // 제출한 accessToken(KEY)로 redis 조회 시 해당 KEY 값이 있고, Value가 logout인 경우
            // 해당 경우는 이미 로그아웃 했고, 이 토큰이 만료될 때 까지 사용할 수 없도록 예외처리
            if (redisUtil.checkData(accessToken) && redisUtil.getData(accessToken).equals("logout")) {
                responseHandler(res, HttpStatus.UNAUTHORIZED, "이미 로그아웃된 사용자입니다. 다시 로그인을 해주세요.");
                return;
            }

            // accessToken 검증 실패 시 refreshToken 을 통해 accessToken 재발급 진행
            if (!jwtUtil.validateToken(accessToken)) {
                log.warn("Token Error, accessToken 검증 실패! refreshToken 으로 accessToken 재발급 시도");
                String refreshToken = jwtUtil.getRefreshToken(req.getHeader("refreshToken"));

                if (StringUtils.hasText(refreshToken) && jwtUtil.validateToken(refreshToken)) {
                    // 최초 로그인 시 redis 에 저장한 refreshToken 을 대조
                    Claims userInfo = jwtUtil.getUserInfoFromToken(refreshToken);
                    log.info("제출한 refresh 토큰 검증 성공, refreshToken 으로 Claims(사용자 정보) 조회");

                    Long userId = Long.valueOf(userInfo.getSubject());
                    String email = userInfo.get("email").toString();
                    String username = userInfo.get("username").toString();
                    String originUsername = userInfo.get("username").toString();
                    String provider = userInfo.get("provider").toString();

                    if (provider.equals("google")) {
                        username += "(google)";
                    } else if (provider.equals("kakao")) {
                        username += "(kakao)";
                    }

                    String redisRefreshToken = redisUtil.getData(username); // 소셜 계정 구분하기!
                    log.info("redis 에서 기존에 저장된 refreshToken 불러오기 성공");

                    // redis 에서 가져온 refreshToken 이 유효하고, 제출한 refreshToken 과 일치한 경우 새 accessToken 재발급
                    if (StringUtils.hasText(redisRefreshToken) && refreshToken.equals(redisRefreshToken)) {
                        log.info("refreshToken 검증 완료");
                        String accessTokenWithBearer = jwtUtil.createAccessToken(userId, originUsername, email, provider);
                        accessToken = jwtUtil.substringToken(accessTokenWithBearer);
                        log.info("accessToken 재발급 성공");

                        res.setHeader("accessToken", accessTokenWithBearer);
                        log.info("헤더에 새로 발급한 AccessToken 저장");
                    }
                } else {
                    // accessToken 검증 실패 후 제출한 refreshToken 의 형식이 올바르지 않거나, redis 의 refreshToken 이 만료되었을 경우
                    log.error("올바르지 않은 refreshToken 형식이거나, refreshToken이 만료되었습니다. 다시 로그인을 해주세요.");
                    responseHandler(res, HttpStatus.UNAUTHORIZED, "올바르지 않은 refreshToken 형식이거나, refreshToken이 만료되었습니다. 다시 로그인을 해주세요.");
                    return;
                }
            }

            try {
                Claims info = jwtUtil.getUserInfoFromToken(accessToken);
                setAuthentication(info.getSubject());
                log.info("로그인 된 사용자");
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
    public void setAuthentication(String userId) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(userId);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // Authentication(인증 객체) 생성하는 메서드
    private Authentication createAuthentication(String userId) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    private void responseHandler(HttpServletResponse response, HttpStatus status, String msg) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(msg);
    }
}
