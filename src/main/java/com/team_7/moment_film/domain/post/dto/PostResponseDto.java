package com.team_7.moment_film.domain.post.dto;

import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private String image;
    private LocalDateTime createdAt;
    private String username;
    private boolean isLiked;
    private List<CommentResponseDTO> commentResponseDTOList;
    public PostResponseDto(Long id, String image,String username, LocalDateTime createdAt, boolean isLiked, List<CommentResponseDTO> commentResponseDTOList){
        this.id = id;
        this.image = image;
        this.createdAt = createdAt;
        this.username = username;
        this.isLiked = isLiked;
        this.commentResponseDTOList = commentResponseDTOList;
    }



    public PostResponseDto(Post post,boolean isLiked){
        this.id = post.getId();
        this.image = post.getImage();
        this.createdAt = post.getCreatedAt();
        this.username = post.getUser().getUsername();
        this.isLiked = isLiked;
    }
}
