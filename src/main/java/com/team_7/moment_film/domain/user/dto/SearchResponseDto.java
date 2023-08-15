package com.team_7.moment_film.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
@Getter
public class SearchResponseDto {
    private Long id;
    private String username;

    @QueryProjection
    public SearchResponseDto(Long id, String username){
        this.id = id;
        this.username = username;
    }
}
