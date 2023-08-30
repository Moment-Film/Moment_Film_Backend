package com.team_7.moment_film.domain.comment.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.team_7.moment_film.domain.subcomment.dto.SubCommentResponseDTO;
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
    private String userImage;
    private String content;
    private List<SubCommentResponseDTO> subComments;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private String createdAt;

    public void initializeSubComments(List<SubCommentResponseDTO> subComments) {
        this.subComments = subComments;
    }
}
