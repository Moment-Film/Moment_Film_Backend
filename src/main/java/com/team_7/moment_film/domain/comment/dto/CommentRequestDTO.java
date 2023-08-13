package com.team_7.moment_film.domain.comment.dto;

import com.team_7.moment_film.domain.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDTO {

    private User writer;
    private Long postId;
    private String content;


}
