package com.team_7.moment_film.domain.user.dto;

import com.team_7.moment_film.domain.post.dto.PostSearchDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SearchResponseDto {
    private Long id;
    private String username;
    private String profileImage;
    private Long postListCnt;
    private Long follower;
    private Long following;
    private List<PostSearchDto> postList = new ArrayList<>();

    public SearchResponseDto(Long id, String username, String profileImage, Long postListCnt, Long follower, Long following){
        this.id = id;
        this.username = username;
        this.profileImage = profileImage;
        this.postListCnt = postListCnt;
        this.follower = follower;
        this.following = following;
    }

    public void setPostList(List<PostSearchDto> postList){
        this.postList = postList;
    }

}
