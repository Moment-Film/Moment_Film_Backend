package com.team_7.moment_film.domain.comment.dto;

import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SubCommentResponseDTO {
    //subcommentId
    private Long id;
    private Long commentId;
    @Lob
    private String content;
    private String username;
    private Long UserId;
    private Post post;
    private Long postId;


}