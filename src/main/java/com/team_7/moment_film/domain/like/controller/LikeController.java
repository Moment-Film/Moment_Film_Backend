package com.team_7.moment_film.domain.like.controller;


import com.team_7.moment_film.domain.like.repository.LikeRepository;
import com.team_7.moment_film.domain.like.service.LikeService;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/post/{postId}")
@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/likes")
    public CustomResponseEntity<?> likePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.likePost(postId, userDetails);
    }

}
