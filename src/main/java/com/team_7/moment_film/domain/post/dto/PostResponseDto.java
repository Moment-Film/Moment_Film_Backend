package com.team_7.moment_film.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.user.entity.User;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String image;
    private String title;
    private String contents;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private String createdAt;
    private String username;
    private int likeCount;
    private int viewCount;
    private int commentCount;
    private User user;
    private Long userId;
    private List<CommentResponseDTO> commentList;

}
