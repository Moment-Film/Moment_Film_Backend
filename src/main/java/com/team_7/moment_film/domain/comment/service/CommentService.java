package com.team_7.moment_film.domain.comment.service;


import com.team_7.moment_film.domain.comment.dto.CommentRequestDTO;
import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.comment.repository.CommentRepository;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.repository.PostRepository;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postrepository;
    private final UserRepository userRepository;
    //댓글 작성 메소드
    public ResponseEntity<ApiResponse> createComment(Long postId, CommentRequestDTO requestDTO, UserDetailsImpl userDetails) {
        try {
            Post post = postrepository.findById(postId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않은 게시글 입니다.")
            );
            User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않은 사용자 입니다.")
            );


            Comment comment = Comment.builder()
                    .post(post)
                    .writer(user)
                    .content(requestDTO.getContent())
                    .build();
            commentRepository.save(comment);
            CommentResponseDTO responseDTO = CommentResponseDTO.builder()
                    .id(comment.getId())
                    .userId(comment.getWriter().getId())
                    .username(comment.getWriter().getUsername())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .build();
            ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.CREATED).data(responseDTO).build();
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("오류 입니다.!" + e.getMessage() + e);
        } catch (Exception e) {
            throw new IllegalArgumentException("서버 오류입니다.!" + e.getMessage() + e);
        }
    }

    // 삭제
    public ResponseEntity<ApiResponse> deleteComment(Long commentId, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않은 댓글입니다.")
        );
        if (!comment.getWriter().getId().equals(userDetails.getUser().getId())) {
            throw new IllegalArgumentException("해당 사용자가 아닙니다.");
        } else {
            commentRepository.delete(comment);
            ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("삭제 성공!").build();
            return ResponseEntity.ok(apiResponse);
        }
    }
}
