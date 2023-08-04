package com.team_7.moment_film.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team_7.moment_film.domain.user.dto.SearchResponseDto;
import com.team_7.moment_film.domain.user.dto.SignupRequestDto;
import com.team_7.moment_film.domain.user.service.KakaoService;
import com.team_7.moment_film.domain.user.service.UserService;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j(topic = "User Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;

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

    // 카카오 인가 코드 API
    @GetMapping("/kakao/callback")
    public CustomResponseEntity<String> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        log.info("카카오 인가 코드 = " + code);
        return kakaoService.kakaoLogin(code, response);
    }
}
