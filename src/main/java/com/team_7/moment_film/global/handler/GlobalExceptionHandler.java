package com.team_7.moment_film.global.handler;

import com.team_7.moment_film.global.dto.CustomResponseEntity;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 클라이언트의 잘못된 요청 exception
    @ExceptionHandler(IllegalArgumentException.class)
    public CustomResponseEntity<String> ExceptionHandler(IllegalArgumentException ex) {
        return CustomResponseEntity.errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 요청한 값이 존재하지 않을 경우의 exception
    @ExceptionHandler(NullPointerException.class)
    public CustomResponseEntity<String> ExceptionHandler(NullPointerException ex) {
        return CustomResponseEntity.errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 인증, 보안 관련 exception
    @ExceptionHandler(SecurityException.class)
    public CustomResponseEntity<String> ExceptionHandler(SecurityException ex) {
        return CustomResponseEntity.errorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // JWT decoding exception
    @ExceptionHandler(MalformedJwtException.class)
    public CustomResponseEntity<String> ExceptionHandler(MalformedJwtException ex) {
        return CustomResponseEntity.errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // JWT 서명 검증 실패 exception
    @ExceptionHandler(SignatureException.class)
    public CustomResponseEntity<String> ExceptionHandelr(SignatureException ex) {
        return CustomResponseEntity.errorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // JWT 만료 exception
    @ExceptionHandler(ExpiredJwtException.class)
    public CustomResponseEntity<String> ExceptionHandler(ExpiredJwtException ex) {
        return CustomResponseEntity.errorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // 지원되지 않는 토큰 형식 exception
    @ExceptionHandler(UnsupportedJwtException.class)
    public CustomResponseEntity<String> ExceptionHandler(UnsupportedJwtException ex) {
        return CustomResponseEntity.errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

}
