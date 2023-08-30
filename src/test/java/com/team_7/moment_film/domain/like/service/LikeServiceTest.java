package com.team_7.moment_film.domain.like.service;

import com.team_7.moment_film.domain.like.repository.LikeRepository;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.service.PostService;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLikePost() {
        Long postId = 1L;
        User mockUser = mock(User.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);

        Post mockPost = new Post();
        when(postService.getPostById(postId)).thenReturn(mockPost);

        when(likeRepository.findByUserIdAndPostId(any(), any())).thenReturn(Optional.empty());

        ApiResponse response = likeService.likePost(postId, userDetails).getBody();

        verify(likeRepository, times(1)).save(any());

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("좋아요!", response.getMsg());
    }


}
