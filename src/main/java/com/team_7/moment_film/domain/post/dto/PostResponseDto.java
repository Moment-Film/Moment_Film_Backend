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
    private Long point;
    private int likeCount;
    private Long viewCount;
    private int commentCount;
    private String username;
    private Long userId;
    private List<User> likeUserId;
    private Long frameId;
    private String filterName;
    private Long filterId;
    private String frameName;
    private List<CommentResponseDTO> commentList;
}
