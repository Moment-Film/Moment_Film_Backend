package com.team_7.moment_film.global.exception;

import com.team_7.moment_film.global.dto.ApiResponse;
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

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 클라이언트의 잘못된 요청 exception
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(IllegalArgumentException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // 요청한 값이 존재하지 않을 경우의 exception
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(NullPointerException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 인증, 보안 관련 exception
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(SecurityException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    // JWT decoding exception
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(MalformedJwtException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // JWT 서명 검증 실패 exception
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse> ExceptionHandelr(SignatureException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    // JWT 만료 exception
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(ExpiredJwtException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    // 지원되지 않는 토큰 형식 exception
    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(UnsupportedJwtException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // 요청 시 전달한 data의 형식이 잘못되었을 경우 exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(MethodArgumentNotValidException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // Security에서 Username으로 User를 찾을 수 없을 때 exception
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(UsernameNotFoundException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    // JPA에서 UserId로 User를 찾을 수 없을 때 exception
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(EntityNotFoundException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    //JSON 변환에 실패하였을 때 exception
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotWritableException(HttpMessageNotWritableException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .msg("JSON 변환에 실패하였습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    // 입출력 작업중 에러, 파일 읽기, 쓰기 중 에러, 네트워크 통신 에러, DB 연결 에러 등 발생하는 exception
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(IOException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .msg("서버의 에러가 발생했습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    // 요청된 데이터 형식이 잘못되었을 때 exception
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotWritableException(HttpMessageNotReadableException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .msg("올바른 JSON 형식의 데이터를 전송해주세요.")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // 필요 파트나 매개변수 누락되었을 때 exception
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse> ExceptionHandler(MissingServletRequestPartException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .msg(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
