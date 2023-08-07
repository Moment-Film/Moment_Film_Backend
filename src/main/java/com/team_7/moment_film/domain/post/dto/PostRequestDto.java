package com.team_7.moment_film.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostRequestDto {
    private Long id;
    private String image;

    @Builder
    public PostRequestDto(Long id, String image){
        this.id = id;
        this.image = image;
    }


}
