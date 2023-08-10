package com.team_7.moment_film.domain.customfilter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class FilterResponseDto {
    private Long id;
    private String blur;
    private String contrast;
    private String grayscale;
    private String sepia;

}
