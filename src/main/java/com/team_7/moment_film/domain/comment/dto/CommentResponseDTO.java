package com.team_7.moment_film.domain.comment.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class CommentResponseDTO implements Serializable {
    private Long id;
    private Long postId;
    private Long userId;
    private User writer;
    private String username;
    private String content;
}
