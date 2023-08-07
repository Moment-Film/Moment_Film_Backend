package com.team_7.moment_film.domain.user.service;

import com.team_7.moment_film.domain.user.dto.PopularUserResponseDto;
import com.team_7.moment_film.domain.user.dto.SearchResponseDto;
import com.team_7.moment_film.domain.user.dto.SignupRequestDto;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j(topic = "User Service")
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 비즈니스 로직
    public CustomResponseEntity<String> signup(SignupRequestDto signupRequestDto) {
        String email = signupRequestDto.getEmail();
        String username = signupRequestDto.getUsername();
        String phone = signupRequestDto.getPhone();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        if (checkEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (checkUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 username 입니다.");
        }
        if (checkPhone(phone)) {
            throw new IllegalArgumentException("이미 존재하는 휴대폰 번호입니다.");
        }

        User user = User.builder()
                .email(email)
                .username(username)
                .password(password)
                .phone(phone)
                .isKakao(false)
                .build();

        userRepository.save(user);

        return CustomResponseEntity.msgResponse(HttpStatus.OK, "회원가입을 축하합니다!");
    }

    // 사용자 검색
    public CustomResponseEntity<List<SearchResponseDto>> searchUser(String userKeyword) {
        return CustomResponseEntity.dataResponse(HttpStatus.OK, userRepository.searchUserByName(userKeyword));
    }

    // 팔로워 많은 순으로 사용자 조회
    public CustomResponseEntity<List<PopularUserResponseDto>> getPopularUser() {
        return CustomResponseEntity.dataResponse(HttpStatus.OK, userRepository.getPopularUser());
    }

    // 이메일 중복 검사 메서드
    private boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Username 중복 검사 메서드
    private boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // 휴대폰 번호 중복 검사 메서드
    private boolean checkPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }


}
