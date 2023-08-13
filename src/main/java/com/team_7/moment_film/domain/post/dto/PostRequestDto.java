package com.team_7.moment_film.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private String contents;
    private MultipartFile image;

    @Builder
    public PostRequestDto(String title, String contents, MultipartFile image) {
        this.title = title;
        this.contents = contents;
        this.image = image;
    }


}
