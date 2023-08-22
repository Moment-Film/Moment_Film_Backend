package com.team_7.moment_film.domain.like.service;


import com.team_7.moment_film.domain.alarm.service.NotificationService;
import com.team_7.moment_film.domain.like.entity.Like;
import com.team_7.moment_film.domain.like.repository.LikeRepository;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.service.PostService;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.team_7.moment_film")
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final NotificationService notificationService;

    @Transactional
    public ResponseEntity<ApiResponse> likePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postService.getPostById(postId);
        User user = userDetails.getUser();
        Optional<Like> optionalLike = likeRepository.findByUserIdAndPostId(user.getId(), post.getId());

        if (optionalLike.isPresent()) {
            likeRepository.deleteByUserIdAndPostId(user.getId(), post.getId());
            ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("좋아요 취소!").build();
            return ResponseEntity.ok(apiResponse);
        } else {
            notificationService.notifyLike(postId);
            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);
            ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("좋아요!").build();
            return ResponseEntity.ok(apiResponse);
        }

    }
}