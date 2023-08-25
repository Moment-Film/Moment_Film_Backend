package com.team_7.moment_film.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdatePasswordDto {
    @Pattern(regexp = "^[a-zA-Z0-9~!@#$%^&*()+|=]{6,10}$", message = "비밀번호는 6자 이상 10자 미만으로 입력해주세요.")
    private String password; // 영문 대/소문자, 숫자, 특수문자 구분 없이 6~10자리의 비밀번호
}