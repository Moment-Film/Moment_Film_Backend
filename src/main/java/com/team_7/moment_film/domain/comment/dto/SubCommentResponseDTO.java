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
    private User writer;
    private Post post;
    private Long postId;

    @Builder
    public SubCommentResponseDTO(Long id, String content, User writer, Post post, Long postId) {
        this.id = id;
        this.content = content;
        this.writer = writer;
        this.post = post;
        this.postId = postId;
    }
}