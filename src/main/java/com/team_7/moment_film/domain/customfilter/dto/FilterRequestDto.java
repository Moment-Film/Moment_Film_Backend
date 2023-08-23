package com.team_7.moment_film.domain.customfilter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FilterRequestDto {
    @NotBlank(message = "필터 이름은 공백일 수 없습니다.")
    private String filterName;

    private String blur;
    private String brightness;
    private String contrast;
    private String saturate;
    private String sepia;

}
