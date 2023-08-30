package com.team_7.moment_film.domain.user.service;

import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;


    // 사용자 정보에 등록된 이메일로 인증 코드 발송
    public ResponseEntity<ApiResponse> sendEmail(User user) {
        // 랜덤 문자열 코드 6자리 생성
        String authCode = UUID.randomUUID().toString().substring(0, 6);
        SimpleMailMessage message = createMessage(user, authCode);

        String key = user.getEmail() + "(" + user.getProvider() + ")";

        // redis에 3분간 인증코드 저장
        Date date = new Date();
        long CODE_EXPIRATION = 3 * 60 * 1000L;
        redisUtil.setData(key, authCode, new Date(date.getTime() + CODE_EXPIRATION));

        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new IllegalArgumentException("메일 전송 오류입니다: " + e.getMessage());
        }

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("code = " + authCode).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 메일 작성
    private SimpleMailMessage createMessage(User user, String authCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Moment Film 비밀번호 재설정 인증코드입니다.");
        message.setText("이메일 인증코드: " + authCode);
        message.setFrom("momentfilm7@naver.com");

        return message;
    }
}