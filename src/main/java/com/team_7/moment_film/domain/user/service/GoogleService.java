package com.team_7.moment_film.domain.user.service;

import com.team_7.moment_film.domain.user.dto.GoogleResponseDto;
import com.team_7.moment_film.domain.user.dto.GoogleUserInfoDto;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.util.JwtUtil;
import com.team_7.moment_film.global.util.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

@Slf4j(topic = "Google Service")
@Service
@RequiredArgsConstructor
public class GoogleService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Value("${google.client-id}")
    private String clientId;
    @Value("${google.client-secret}")
    private String clientSecret;
    @Value("${google.redirect-uri}")
    private String redirectUri;

    // 구글 로그인 서비스 로직
    public ResponseEntity<ApiResponse> googleLogin(String code, HttpServletResponse response) {
        // AccessToken 요청
        ResponseEntity<GoogleResponseDto> token = getToken(code);

        if (token == null) {
            throw new NullPointerException("access_token 요청에 실패했습니다.");
        }

        String googleAccessToken = token.getBody().getAccess_token();

        // 사용자 정보 요청
        ResponseEntity<GoogleUserInfoDto> userInfo = getUserInfo(googleAccessToken);

        if (userInfo == null) {
            throw new NullPointerException("사용자 정보 조회를 실패했습니다.");
        }

        String email = userInfo.getBody().getEmail();
        String username = userInfo.getBody().getGiven_name();

        // email 값으로 User 객체 조회
        User checkUser = userRepository.findByEmailAndProvider(email, "google").orElse(null);

        // 회원가입 진행 (가입된 계정이 없는 경우)
        if (checkUser == null) {
            log.info("회원가입 시도");
            signup(email, username);
        }

        // 로그인 진행
        User user = userRepository.findByEmailAndProvider(email, "google").orElse(null);
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getEmail(), user.getProvider());
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUsername(), user.getEmail(), user.getProvider());

        log.info("JWT 발급 : accessToken = " + accessToken);
        log.info("JWT 발급 : refreshToken = " + refreshToken);

        String refreshTokenValue = jwtUtil.substringToken(refreshToken);
        Date expiration = jwtUtil.getUserInfoFromToken(refreshTokenValue).getExpiration();

        redisUtil.setData(user.getUsername() + "(google)", refreshTokenValue, expiration);
        log.info("Redis에 RefreshToken 저장(구글 계정)");

        response.setHeader("accessToken", accessToken);
        response.setHeader("refreshToken", refreshToken);

        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK)
                .msg("구글 로그인 성공")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    // 회원가입 로직
    private User signup(String email, String username) {
        String password = passwordEncoder.encode(UUID.randomUUID().toString());

        User newUser = User.builder()
                .email(email)
                .password(password)
                .username(username)
                .phone(null)
                .provider("google")
                .point(1000L)
                .build();

        userRepository.save(newUser);
        log.info("구글 계정 회원가입 성공");
        return newUser;
    }

    // 사용자 정보 요청 로직
    private ResponseEntity<GoogleUserInfoDto> getUserInfo(String accessToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com")
                .path("/userinfo/v2/me")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserInfoDto> response = restTemplate.exchange(uri, HttpMethod.GET, request, GoogleUserInfoDto.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response;
        }
        log.info("사용자 정보 " + response.getBody());
        return null;
    }

    // AccessToken 요청 로직
    private ResponseEntity<GoogleResponseDto> getToken(String code) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com")
                .path("/token")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", redirectUri);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        ResponseEntity<GoogleResponseDto> response = restTemplate.exchange(requestEntity, GoogleResponseDto.class);
        log.info("status " + response.getStatusCode());
        log.info("access_token " + response.getBody().getAccess_token());
        log.info("token_type " + response.getBody().getToken_type());
        log.info("expires_in " + response.getBody().getExpires_in());
        log.info("scope " + response.getBody().getScope());
        log.info("id_token " + response.getBody().getId_token());

        if (response.getStatusCode() == HttpStatus.OK) {
            return response;
        }
        return null;
    }
}
