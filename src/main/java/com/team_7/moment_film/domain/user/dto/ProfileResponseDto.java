package com.team_7.moment_film.domain.user.dto;

import com.team_7.moment_film.domain.post.entity.Post;
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
    private List<Post> postList;
    private int postListCnt;
    private List<Post> likePosts;
}
