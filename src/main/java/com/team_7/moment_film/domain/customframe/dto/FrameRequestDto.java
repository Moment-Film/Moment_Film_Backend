package com.team_7.moment_film.domain.customframe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FrameRequestDto {
    @NotBlank(message = "프레임 이름은 공백일 수 없습니다.")
    private String frameName;

    private String hue;
    private String saturation;
    private String lightness;

}
