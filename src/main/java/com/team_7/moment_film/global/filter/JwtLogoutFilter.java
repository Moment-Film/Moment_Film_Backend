package com.team_7.moment_film.global.filter;

import com.team_7.moment_film.global.util.JwtUtil;
import com.team_7.moment_film.global.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Slf4j(topic = "로그아웃 필터")
@RequiredArgsConstructor
public class JwtLogoutFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("클라이언트 요청 URI 확인 : " + request.getRequestURI());
        // 클라이언트의 요청 URI가 로그아웃일 때만 아래의 로직을 수행하고, 아닌 경우 다음 인가 필터로 이동
        if (request.getRequestURI().equals("/api/user/logout")) {
            String accessToken = jwtUtil.substringToken(request.getHeader("accessToken"));
            String refreshToken = jwtUtil.substringToken(request.getHeader("refreshToken"));
            // AccessToken, RefreshToken 유효성 확인 및 검증
            if (!jwtUtil.validateToken(accessToken) && !jwtUtil.validateToken(refreshToken)) {
                // 토큰 검증 실패한 경우
                log.error("로그아웃 실패, 유효하지 않은 토큰입니다.");
                responseHandler(response, "로그아웃 실패, 유효하지 않은 토큰입니다.");
                return;
            }

            String username = jwtUtil.getUserInfoFromToken(refreshToken).get("username").toString();
            if (redisUtil.getData(username) == null) {
                // redis에 저장된 refreshToken이 없는 경우 -> 만료된 사용자
                log.error("로그아웃 실패, 이미 만료된 사용자입니다.");
                responseHandler(response, "로그아웃 실패, 이미 만료된 사용자입니다.");
                return;
            }
            redisUtil.deleteData(username);
            log.info("Redis에 저장된 RefreshToken 삭제");

            // Redis에 key : AccessToken, value : logout, expire : AccessToken의 남은 만료 시간을 저장 -> 블랙리스트 추가하여 해당 시간동안 토큰 못쓰게 막음
            Date accessTokenExpiration = jwtUtil.getUserInfoFromToken(accessToken).getExpiration();
            redisUtil.setData(accessToken, "logout", accessTokenExpiration);
            log.info("해당 AccessToken을 남은 유효기간 동안 Redis에 저장");
            // SecurityContextHolder에 setting한 사용자 정보를 비움
            SecurityContextHolder.clearContext();

            // logout response 반환
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("로그아웃 성공");
            return;
        }

        // 로그아웃 요청이 아니면 바로 다음 필터로
        filterChain.doFilter(request, response);
    }

    private void responseHandler(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(msg);
    }
}








