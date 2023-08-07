package com.team_7.moment_film.domain.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequestDTO {

    private Long userId;
    private Long parentId;
    private String content;

    public CommentRequestDTO(String content) {
        this.content = content;
    }
}
