package com.team_7.moment_film.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private Long id;
    private String title;
    private String contents;
    private String image;

    @Builder
    public PostRequestDto(Long id, String image, String title, String contents){
        this.id = id;
        this.image = image;
        this.title = title;
        this.contents = contents;
    }


}
