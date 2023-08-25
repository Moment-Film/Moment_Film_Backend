package com.team_7.moment_film.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String requestUri = request.getRequestURI();
        String errorMessage;

        if (isSupportedUri(requestUri)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            errorMessage = "로그인이 필요합니다.";
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            errorMessage = "존재하지 않는 URL입니다.";
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorMessage);
    }

    // 클라이언트가 접근한 uri가 서버에서 제공하는 uri인지 확인하는 메서드
    private boolean isSupportedUri(String requestUri) {
        List<String> supportedUri = Arrays.asList(
                "/api/user/signup", // 회원가입
                "/api/user/withdrawal", // 회원탈퇴
                "/api/user/login", // 로그인
                "/api/user/logout", // 로그아웃
                "/api/user/kakao/callback", // 카카오 로그인
                "/api/user/google/callback", // 구글 로그인
                "/api/user/search", // 사용자 검색
                "/api/user/popular", // 인기 사용자 조회
                "/api/follow/{userId}", // 사용자 팔로우
                "/api/user/profile/{userId}", // 사용자 프로필 조회
                "/api/user/info", // 사용자 개인정보 조회
                "/api/user/password-reset", // 비밀번호 변경
                "/api/user/email", // 비밀번호 인증 코드 메일 전송
                "/api/user/point", // 포인트 적립/차감
                "/api/post", // 게시글 조회
                "/api/post/like", // 게시글 좋아요순 조회
                "/api/post/view", // 게시글 조회순 조회
                "/api/post/{postId}", // 게시글 상세 조회
                "/api/post/{postId}/likes", //게시글 좋아요/취소
                "/api/post/{postId}/comment", // 댓글 작성
                "/api/post/{postId}/comment/{commentId}", // 댓글 삭제
                "/api/post/{postId}/comment/{commentId}/subcomment", // 대댓글 작성
                "/api/post/{postId}/comment/{commentId}/subcomment/{subcommentId}", // 대댓글 삭제
                "/api/filter", // 필터 등록, 내가 만든 필터 조회
                "/api/filter/{filterId}", // 특정 필터 적용
                "/api/frame", // 프레임 등록, 내가 만든 프레임 조회
                "/api/frame/{frameId}" // 특정 프레임 적용
        );

        return supportedUri.stream().anyMatch(urlPattern -> antPathMatcher.match(urlPattern, requestUri));
    }
}
