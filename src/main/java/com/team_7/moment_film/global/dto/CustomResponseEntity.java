package com.team_7.moment_film.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@Getter
// 공통으로 사용되는 Response DTO이므로 어떤 값(DTO)이 들어올지 모르기 때문에 Generic을 사용.
public class CustomResponseEntity<T> {
    private HttpStatus status;
    private String msg;
    private T body;

    // DTO에 객체 정보를 반환해야 하는 경우  ->  EX)게시글 상세 정보
    public static <T> CustomResponseEntity<T> dataResponse(HttpStatus status, T responseDto) {
        return CustomResponseEntity.<T>builder()
                .status(status)
                .body(responseDto)
                .build();
    }

    // DTO에 MSG만 반환해야 하는 경우  ->  EX)회원가입 성공!
    public static <T> CustomResponseEntity<T> msgResponse(HttpStatus status, String msg){
        return CustomResponseEntity.<T>builder()
                .status(status)
                .msg(msg)
                .build();
    }

    // Exception 발생 시 클라이언트에 반환할 DTO  -> EX) status 400, msg : 잘못된 요청입니다.
    public static <T> CustomResponseEntity<T> errorResponse(HttpStatus status, String msg){
        return CustomResponseEntity.<T>builder()
                .status(status)
                .msg(msg)
                .build();
    }
}
