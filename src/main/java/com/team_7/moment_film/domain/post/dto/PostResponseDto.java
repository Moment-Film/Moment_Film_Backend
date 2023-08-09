package com.team_7.moment_film.domain.post.dto;

import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
public class PostResponseDto {
    private Long id;
    private String image;
    private String title;
    private String contents;
    private LocalDateTime createdAt;
    private String username;
    private boolean isLiked;
    private List<CommentResponseDTO> commentResponseDTOList;
    private Integer likeCount;
    private Integer viewCount;
    private User user;

    @Builder
    public PostResponseDto(Long id, String image,String username, LocalDateTime createdAt, boolean isLiked, List<CommentResponseDTO> commentResponseDTOList
    ,Integer likeCount, Integer viewCount, String title, String contents, User user){
        this.id = id;
        this.image = image;
        this.createdAt = createdAt;
        this.username = username;
        this.isLiked = isLiked;
        this.commentResponseDTOList = commentResponseDTOList;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.title = title;
        this.contents = contents;
        this.user = user;
    }

    public PostResponseDto(Post post){
        this.id = post.getId();
        this.image = post.getImage();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.createdAt = post.getCreatedAt();
        this.username = post.getUser().getUsername();
    }

    public PostResponseDto(Post post,boolean isLiked){
        this.id = post.getId();
        this.image = post.getImage();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.createdAt = post.getCreatedAt();
        this.username = post.getUser().getUsername();
        this.isLiked = isLiked;
    }
}
