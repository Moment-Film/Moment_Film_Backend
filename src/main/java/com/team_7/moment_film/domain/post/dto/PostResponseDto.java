package com.team_7.moment_film.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private String createdAt;
    private String username;
    private Integer likeCount;
    private Integer viewCount;
    private Integer commentCount;
    private User user;
    private List<Comment> commentList;
    @Builder
    public PostResponseDto(Long id, String image, String username, String createdAt
            , Integer likeCount, Integer viewCount, Integer commentCount, String title, String contents,
                           User user, List<Comment> commentList) {
        this.id = id;
        this.image = image;
        this.createdAt = createdAt;
        this.username = username;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.commentCount = commentCount;
        this.commentList = commentList;
    }

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.image = post.getImage();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.createdAt = post.getCreatedAt();
        this.username = post.getUser().getUsername();
    }
}
