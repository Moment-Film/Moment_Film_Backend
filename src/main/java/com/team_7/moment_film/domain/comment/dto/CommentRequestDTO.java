package com.team_7.moment_film.domain.comment.dto;

import com.team_7.moment_film.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {

    private User writer;
    private Long postId;
    private String content;

    // 기본 생성자 추가
    public CommentRequestDTO() {
    }


    public CommentRequestDTO(String content, User writer, Long postId) {
        this.writer = writer;
        this.content = content;
        this.postId = postId;
    }
}
