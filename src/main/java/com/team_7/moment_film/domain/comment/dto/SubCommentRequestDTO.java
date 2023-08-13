package com.team_7.moment_film.domain.comment.dto;

import com.team_7.moment_film.domain.user.entity.User;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCommentRequestDTO {
    private Long commentId;
    @Lob
    private String content;
    private User writer;
    private Long PostId;
    private Long userId;

}