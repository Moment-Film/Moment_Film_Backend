package com.team_7.moment_film.domain.user.dto;

import com.team_7.moment_film.domain.post.entity.TempPost;
import com.team_7.moment_film.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProfileResponseDto {
    private Long id;
    private String username;
    private List<User> followerList;
    private List<User> followingList;
    private List<TempPost> postList;
    private int postListCnt;
    private List<TempPost> likePosts;
}
