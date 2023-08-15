package com.team_7.moment_film.domain.comment.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class CommentResponseDTO implements Serializable {
    private Long id;
    private Long userId;
    private String username;
    private String content;
    private List<SubCommentResponseDTO> subComments;


    public void initializeSubComments(List<SubCommentResponseDTO> subComments) {
        this.subComments = subComments;
    }
}
