package com.team_7.moment_film.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
@Getter
public class SearchResponseDto {
    private String username;

    @QueryProjection
    public SearchResponseDto(String username){
        this.username = username;
    }
}
