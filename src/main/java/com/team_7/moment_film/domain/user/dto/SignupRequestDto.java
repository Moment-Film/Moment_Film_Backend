package com.team_7.moment_film.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequestDto {
    @NotBlank
    @Size(min = 2, max = 10, message = "username은 2자 이상 10자 미만으로 입력해주세요.")
    private String username; // 공백 x 한/영(2자 이상 ~ 10자 미만)

    @NotBlank
    @Size(min = 6, max = 10, message = "비밀번호는 6자 이상 10자 미만으로 입력해주세요.")
    private String password; // 소문자/대문자/숫자 구분없이 (6자 이상 ~ 10자 미만)

    @Email(message = "이메일 주소 형식이 아닙니다.")
    @Size(max = 20, message = "이메일 주소는 1자 이상 20자 이하로 입력해주세요.")
    private String email; // 문자 check@문자 check.com (총길이 = 20자 내)

    @Pattern(regexp = "^010[0-9]{8}$", message = "유효한 휴대폰 번호 형식이 아닙니다.")
    private String phone; // 01012345678 (11자)
}
