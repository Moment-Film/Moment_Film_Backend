package com.team_7.moment_film.domain.post.dto;

import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.customfilter.entity.Filter;
import com.team_7.moment_film.domain.customframe.entity.Frame;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private Integer likeCount;
    private Integer viewCount;
    private Integer commentCount;
    private User user;
    private List<Comment> commentList;
    private Long user_Id;
    @Builder
    public PostResponseDto(Long id, String image, String username, LocalDateTime createdAt
            , Integer likeCount, Integer viewCount, Integer commentCount, String title, String contents,
                           User user, List<Comment> commentList,Long user_Id) {
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
        this.user_Id = user_Id;
    }

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.image = post.getImage();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.createdAt = post.getCreatedAt();
        this.username = post.getUser().getUsername();
    }

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
}
