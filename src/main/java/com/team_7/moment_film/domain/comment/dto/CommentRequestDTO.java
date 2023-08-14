package com.team_7.moment_film.domain.comment.dto;

import com.team_7.moment_film.domain.user.entity.User;
import lombok.*;

@Getter
@AllArgsConstructor
public class CommentRequestDTO {
    private String content;
}
