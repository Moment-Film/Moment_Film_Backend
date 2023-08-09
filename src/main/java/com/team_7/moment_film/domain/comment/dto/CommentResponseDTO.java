package com.team_7.moment_film.domain.comment.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CommentResponseDTO implements Serializable {
    private Long id;
    private String content;
    private Post post;
    private String username;
    private Long userId;
    private Long postId;
}
