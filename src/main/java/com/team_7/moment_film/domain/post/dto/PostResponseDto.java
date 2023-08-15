package com.team_7.moment_film.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.like.entity.Like;
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
    private int likeCount;
    private Long viewCount;
    private int commentCount;
    private String username;
    private Long userId;
    private List<CommentResponseDTO> commentList;
    private Long filterId;
    private Long frameId;
    private String filterName;
    private String frameName;
    private List<Like> LikeId;
}
