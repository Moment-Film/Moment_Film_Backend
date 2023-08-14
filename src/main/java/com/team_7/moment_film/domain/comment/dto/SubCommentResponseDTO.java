package com.team_7.moment_film.domain.comment.dto;

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
    private Long UserId;
    private Long commentId;
    private String content;
    private String username;
}