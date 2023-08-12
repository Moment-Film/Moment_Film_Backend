package com.team_7.moment_film.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class PopularUserResponseDto {
    private Long id;
    private String username;
    private Long follower;

    @QueryProjection
    public PopularUserResponseDto(Long id, String username, Long follower){
        this.id = id;
        this.username = username;
        this.follower = follower;
    }

}
