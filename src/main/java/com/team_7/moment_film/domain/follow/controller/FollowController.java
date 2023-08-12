package com.team_7.moment_film.domain.follow.controller;

import com.team_7.moment_film.domain.follow.service.FollowService;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    //팔로우 추가 기능
    @PostMapping("/{userId}")
    public CustomResponseEntity<String> followUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return followService.followUser(userDetails.getUser(),userId);
    }

    //팔로우 취소 기능
    @DeleteMapping("/{userId}")
    public CustomResponseEntity<String> unfollowUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return followService.unfollowUser(userDetails.getUser(),userId);
    }
}
