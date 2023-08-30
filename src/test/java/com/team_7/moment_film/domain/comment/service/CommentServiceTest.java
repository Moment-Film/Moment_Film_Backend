package com.team_7.moment_film.domain.comment.service;

import com.team_7.moment_film.domain.comment.dto.CommentRequestDTO;
import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.comment.repository.CommentRepository;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.repository.PostRepository;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@RequiredArgsConstructor
class CommentServiceTest {


    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void testCreateCommentSuccess() {
        Long postId = 1L;
        CommentRequestDTO requestDTO = new CommentRequestDTO();
        UserDetailsImpl userDetails = new UserDetailsImpl(mock(User.class));

        Post mockPost = new Post();
        User mockUser = mock(User.class);
        Comment mockComment = mock(Comment.class);


        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        ResponseEntity<ApiResponse> responseEntity = commentService.createComment(postId, requestDTO, userDetails);


        verify(commentRepository, times(1)).save(any(Comment.class));


        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void testDeleteComment() {
        Long commentId = 1L;
        UserDetailsImpl userDetails = new UserDetailsImpl(mock(User.class));

        Comment mockComment = Comment.builder()
                .writer(userDetails.getUser())
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

        ResponseEntity<ApiResponse> responseEntity = commentService.deleteComment(commentId, userDetails);

        verify(commentRepository, times(1)).delete(any(Comment.class));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

}