package com.team_7.moment_film.domain.comment.dto;

import com.team_7.moment_film.domain.user.entity.User;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubCommentRequestDTO {
    private Long commentId;
    @Lob
    private String content;
    private User writer;
    private Long PostId;

    public SubCommentRequestDTO(Long CommentId,String content, User writer, Long postId) {
        this.commentId = CommentId;
        this.writer = writer;
        this.content = content;
        this.PostId = postId;
    }
}