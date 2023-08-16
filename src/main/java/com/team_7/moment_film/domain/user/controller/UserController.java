package com.team_7.moment_film.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team_7.moment_film.domain.user.dto.SignupRequestDto;
import com.team_7.moment_film.domain.user.dto.UpdateRequestDto;
import com.team_7.moment_film.domain.user.service.GoogleService;
import com.team_7.moment_film.domain.user.service.KakaoService;
import com.team_7.moment_film.domain.user.service.MailService;
import com.team_7.moment_film.domain.user.service.UserService;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "User Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final MailService mailService;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    // 사용자 검색 API
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchUser(@RequestParam String userKeyword) {
        return userService.searchUser(userKeyword);
    }

    // 프로필 조회 API
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse> getProfile(@PathVariable Long userId) {
        return userService.getProfile(userId);
    }

    // 카카오 로그인 API
    @PostMapping("/kakao/callback")
    public ResponseEntity<ApiResponse> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        log.info("카카오 인가 코드 = " + code);
        return kakaoService.kakaoLogin(code, response);
    }

    // 구글 로그인 API
    @PostMapping("/google/callback")
    public ResponseEntity<ApiResponse> googleLogine(@RequestParam String code, HttpServletResponse response) {
        log.info("구글 인가 코드 = "+ code);
        return googleService.googleLogin(code, response);
    }


    // 인기 많은 사용자 조회 API (팔로워 순)
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse> getPopularUser() {
        return userService.getPopularUser();
    }


    // 개인 정보 조회 API
    @GetMapping("/info")
    public ResponseEntity<ApiResponse> getInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getInfo(userDetails.getUser());
    }

    // 개인 정보 수정 API
    @PutMapping("/info")
    public ResponseEntity<ApiResponse> updateInfo(@Valid @RequestBody UpdateRequestDto requestDto,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.updateInfo(requestDto, userDetails.getUser());
    }

    // 메일 전송 API
    @PostMapping("/email")
    public ResponseEntity<ApiResponse> sendEmail(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mailService.sendEmail(userDetails.getUser());
    }

    // 비밀번호 재설정 API
    @PutMapping("/password-reset")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody UpdateRequestDto requestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestParam String code) {
        return userService.resetPassword(requestDto, userDetails.getUser(), code);
    }

    // 회원 탈퇴 API
    @DeleteMapping("/withdrawal")
    public ResponseEntity<ApiResponse> withdrawal(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        return userService.withdrawal(userDetails.getUser(), accessToken);
    }
}
