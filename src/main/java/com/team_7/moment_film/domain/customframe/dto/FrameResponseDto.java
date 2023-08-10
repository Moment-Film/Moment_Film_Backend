package com.team_7.moment_film.domain.customframe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FrameResponseDto {
    private Long id;
    private String frameName;
    private String image;
}
