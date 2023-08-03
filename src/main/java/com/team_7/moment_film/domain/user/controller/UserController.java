package com.team_7.moment_film.domain.user.controller;

import com.team_7.moment_film.domain.user.dto.SignupRequestDto;
import com.team_7.moment_film.domain.user.service.UserService;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "User Controlelr")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    // 회원가입 API
    @PostMapping("/signup")
    public CustomResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }
}
