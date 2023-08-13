package com.team_7.moment_film.global.handler;

import com.team_7.moment_film.global.dto.CustomResponseEntity;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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

    // 요청 시 전달한 data의 형식이 잘못되었을 경우 exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CustomResponseEntity<String> ExceptionHandler(MethodArgumentNotValidException ex) {
        return CustomResponseEntity.errorResponse(HttpStatus.BAD_REQUEST, ex.getFieldError().getDefaultMessage());
    }

    // Security에서 Username으로 User를 찾을 수 없을 때 exception
    @ExceptionHandler(UsernameNotFoundException.class)
    public CustomResponseEntity<String> ExceptionHandler(UsernameNotFoundException ex) {
        return CustomResponseEntity.errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // JPA에서 UserId로 User를 찾을 수 없을 때 exception
    @ExceptionHandler(EntityNotFoundException.class)
    public CustomResponseEntity<String> ExceptionHandler(EntityNotFoundException ex) {
        return CustomResponseEntity.errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    //JSON 변환에 실패하였을 때 exception
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<String> handleHttpMessageNotWritableException(HttpMessageNotWritableException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("JSON 변환에 실패하였습니다.");
    }

    // 요청된 데이터 형식이 잘못되었을 때 exception
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotWritableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("올바른 JSON 형식의 데이터를 전송해주세요.");
    }

    // 필요 파트나 매개변수 누락되었을 때 exception
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<String> ExceptionHandler(MissingServletRequestPartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
