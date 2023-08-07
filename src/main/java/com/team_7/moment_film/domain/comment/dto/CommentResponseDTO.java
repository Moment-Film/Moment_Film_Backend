package com.team_7.moment_film.domain.comment.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.user.dto.LoginRequestDto;
import com.team_7.moment_film.domain.user.entity.User;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@Data
@Getter
@NoArgsConstructor
public class CommentResponseDTO implements Serializable {

    private Long id;
    private String content;
    private User writer;
    @JsonIgnore
    private List<CommentResponseDTO> children = new ArrayList<>();

    public CommentResponseDTO(Long id, String content, User writer) {
        this.id = id;
        this.content = content;
        this.writer = writer;
    }

//    public static CommentResponseDTO convertCommentToDto(Comment comment){
//        return comment.getIsDeleted() ?
//                new CommentResponseDTO(comment.getId(), "삭제된 댓글입니다.",null) :
//                new CommentResponseDTO(comment.getId(), comment.getContent(), comment.getWriter());
//    }
    public static CommentResponseDTO convertCommentToDto(Comment comment){
        if (comment.getIsDeleted()) {
            return new CommentResponseDTO(comment.getId(), "삭제된 댓글입니다.", null);
        } else {
            CommentResponseDTO dto = new CommentResponseDTO(comment.getId(), comment.getContent(), comment.getWriter());
            // 자식 댓글들을 재귀적으로 추가하지 않음
            return dto;
        }
    }
}
