package com.team_7.moment_film.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PopularUserResponseDto {
    private Long id;
    private String username;
    private String profileImage;
    private Long follower;
    private Long postCnt;

}