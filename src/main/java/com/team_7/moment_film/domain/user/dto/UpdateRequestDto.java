package com.team_7.moment_film.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateRequestDto {
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message = "username은 2자 이상 10자 미만으로 입력해주세요.")
    private String username; // 한글, 영문 대/소문자, 숫자 구분 없이 2~10자리의 유저네임

    @Pattern(regexp = "^010[0-9]{8}$", message = "유효한 휴대폰 번호 형식이 아닙니다.")
    private String phone; // 010으로 시작하는 11자리의 숫자
}