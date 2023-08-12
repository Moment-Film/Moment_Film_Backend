package com.team_7.moment_film.domain.post.entity;

import com.querydsl.core.annotations.QueryProjection;
import com.team_7.moment_film.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TempPost {
    private Long id;
    private String title;
    private String contents;
    private String image;
    private Long userId;
    private String username;

    @QueryProjection
    public TempPost(Long id, String title, String contents, String image) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.image = image;
    }

    @QueryProjection
    public TempPost(Long id, String title, String contents, String image, User user) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.userId = user.getId();
        this.username = user.getUsername();
    }
}
