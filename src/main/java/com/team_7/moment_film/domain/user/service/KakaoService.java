package com.team_7.moment_film.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.team_7.moment_film.domain.user.dto.KakaoUserInfoDto;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "Kakao Service")
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    @Value("${kakao.restapi.key}")
    private String restApiKey;
    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    public ResponseEntity<ApiResponse> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. 카카오 서버로 accessToken 요청
        String token = getToken(code);
        log.info("카카오 엑세스 토큰 = " + token);

        // 2. 카카오 서버로 유저의 데이터 요청
        KakaoUserInfoDto kakaoUserInfo = getUserInfo(token);
        log.info("카카오 유저 이메일 = " + kakaoUserInfo.getEmail());
        log.info("카카오 유저 이름 = " + kakaoUserInfo.getUsername());
        log.info("카카오 유저 id = " + kakaoUserInfo.getId());


        // 3. email 로 회원가입 여부 판단
        if (!userRepository.existsByEmail(kakaoUserInfo.getEmail())) {
            // 3-1. 받아온 유저의 데이터로 회원가입
            User kakaoUser = signupKakaoUser(kakaoUserInfo);
        }

        String email = kakaoUserInfo.getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Long id = user.getId();
        String username = user.getUsername();

        // 4. 로그인(JWT 발급)
        String accessToken = jwtUtil.createAccessToken(id, username, email);
        String refreshToken = jwtUtil.createRefreshToken(id, username, email);

        log.info("JWT 발급 : accessToken = " + accessToken);
        log.info("JWT 발급 : refreshToken = " + refreshToken);

        response.setHeader("accessToken", accessToken);
        response.setHeader("refreshToken", refreshToken);

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("카카오 로그인 성공").build();
        return ResponseEntity.ok(apiResponse);
    }

    // 카카오 계정으로 회원가입(신규만)
    private User signupKakaoUser(KakaoUserInfoDto kakaoUserInfo) {
        log.info("회원가입 시도");
        String password = UUID.randomUUID().toString();
        String pwEncode = passwordEncoder.encode(password);

        if (userRepository.existsByUsername(kakaoUserInfo.getUsername())) {
            log.error("유저네임 중복 발생!", kakaoUserInfo.getUsername());
            throw new IllegalArgumentException("이미 존재하는 username 입니다.");
        }

        User newUser = User.builder()
                .email(kakaoUserInfo.getEmail())
                .username(kakaoUserInfo.getUsername())
                .password(pwEncode)
                .phone(null) // 카카오 비즈앱 전환 시 가져올 수 있음.
                .isKakao(true)
                .build();

        userRepository.save(newUser);
        log.info("카카오 계정으로 회원가입 성공!");
        return newUser;
    }

    // 카카오 user info 받아오는 메서드
    private KakaoUserInfoDto getUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URI
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // 요청 Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 객체 생성
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // 요청 후 받을 응답 객체
        ResponseEntity<JsonNode> response = restTemplate.exchange(requestEntity, JsonNode.class);

        // 응답 결과(StatusCode, body)
        log.info("Status Code " + response.getStatusCode());

        // 정보 확인
        Long id = response.getBody().get("id").asLong();
        String email = response.getBody().get("kakao_account").get("email").asText();
        String nickname = response.getBody().get("properties").get("nickname").asText();

        if (!StringUtils.hasText(email)) {
            log.info("동의 항목 중 이메일 동의하지 않았음");
        }

        // 응답 객체에서 필요한 정보로 DTO 생성
        KakaoUserInfoDto kakaoUserInfoDto = KakaoUserInfoDto.builder()
                .id(id)
                .username(nickname)
                .email(email)
                .build();

        return kakaoUserInfoDto;
    }

    // 카카오 accessToken 받아오는 메서드
    private String getToken(String code) {
        // 요청 URI
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // 요청 Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 Body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", restApiKey);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        // 요청 객체 생성(method, header, body)
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // 응답 객체(JSON 객체)
        ResponseEntity<JsonNode> response = restTemplate.exchange(requestEntity, JsonNode.class);

        return response.getBody().get("access_token").toString();
    }
}
