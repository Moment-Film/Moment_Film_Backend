package com.team_7.moment_film.domain.comment.dto;


import com.team_7.moment_film.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private List<SubCommentResponseDTO> subComments;


    public CommentResponseDTO(Long id, String content) {
        this.id = id;
        this.content = content;
        this.subComments = new ArrayList<>();
    }

    public void initializeSubComments(List<SubCommentResponseDTO> subComments) {
        this.subComments = subComments;
    }
}
