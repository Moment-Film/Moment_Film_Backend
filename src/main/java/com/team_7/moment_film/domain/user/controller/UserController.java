package com.team_7.moment_film.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team_7.moment_film.domain.user.dto.*;
import com.team_7.moment_film.domain.user.service.KakaoService;
import com.team_7.moment_film.domain.user.service.MailService;
import com.team_7.moment_film.domain.user.service.UserService;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "User Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;
    private final MailService mailService;

    // 회원가입 API
    @PostMapping("/signup")
    public CustomResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    // 사용자 검색 API
    @GetMapping("/search")
    public CustomResponseEntity<List<SearchResponseDto>> searchUser(@RequestParam String userKeyword) {
        return userService.searchUser(userKeyword);
    }

    // 프로필 조회 API
    @GetMapping("/profile/{userId}")
    public CustomResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long userId) {
        return userService.getProfile(userId);
    }

    // 카카오 인가 코드 API
    @PostMapping("/kakao/callback")
    public CustomResponseEntity<String> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        log.info("카카오 인가 코드 = " + code);
        return kakaoService.kakaoLogin(code, response);
    }

    // 인기 많은 사용자 조회 API (팔로워 순)
    @GetMapping("/popular")
    public CustomResponseEntity<List<PopularUserResponseDto>> getPopularUser() {
        return userService.getPopularUser();
    }


    // 개인 정보 조회 API
    @GetMapping("/info")
    public CustomResponseEntity<UserInfoDto> getInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getInfo(userDetails.getUser());
    }

    // 개인 정보 수정 API
    @PutMapping("/info")
    public CustomResponseEntity<String> updateInfo(@Valid @RequestBody UpdateRequestDto requestDto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.updateInfo(requestDto, userDetails.getUser());
    }

    // 메일 전송 API
    @PostMapping("/email")
    public CustomResponseEntity<String> sendEmail(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return mailService.sendEmail(userDetails.getUser());
    }

    // 비밀번호 재설정 API
    @PutMapping("/password-reset")
    public CustomResponseEntity<String> resetPassword(@Valid @RequestBody UpdateRequestDto requestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestParam String code) {
        return userService.resetPassword(requestDto, userDetails.getUser(), code);
    }

    // 회원 탈퇴 API
    @DeleteMapping("/withdrawal")
    public CustomResponseEntity<String> withdrawal(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        return userService.withdrawal(userDetails.getUser(), accessToken);
    }
}
