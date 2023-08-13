package com.team_7.moment_film.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@Getter
// 공통으로 사용되는 Response DTO이므로 어떤 값(DTO)이 들어올지 모르기 때문에 Generic을 사용.
public class ApiResponse<T> {
    private HttpStatus status;
    private String msg;
    private T data;
}
