package com.team_7.moment_film.domain.comment.service;


import com.team_7.moment_film.domain.comment.dto.SubCommentRequestDTO;
import com.team_7.moment_film.domain.comment.dto.SubCommentResponseDTO;
import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.comment.entity.SubComment;
import com.team_7.moment_film.domain.comment.repository.CommentRepository;
import com.team_7.moment_film.domain.comment.repository.SubCommentRepository;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SubCommentService {


    private final CommentRepository commentRepository;
    private final SubCommentRepository subCommentRepository;

    // 대댓글 생성
    public ResponseEntity<ApiResponse> createSubComment(Long commentId, SubCommentRequestDTO requestDTO, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않은 댓글입니다.")
        );
        User writer = userDetails.getUser();
        SubComment subComment = SubComment.builder()
                .writer(writer)
                .comment(comment)
                .content(requestDTO.getContent())
                .build();
        subCommentRepository.save(subComment);
        SubCommentResponseDTO responseDTO = SubCommentResponseDTO.builder()
                .id(subComment.getId())
                .commentId(comment.getId())
                .content(subComment.getContent())
                .createdAt(subComment.getCreatedAt())
                .build();
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.CREATED).data(responseDTO).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // 대댓글 삭제
    public ResponseEntity<ApiResponse> deleteSubComment(Long subcommentId, UserDetailsImpl userDetails) {
        SubComment subComment = subCommentRepository.findById(subcommentId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 대댓글입니다.")
        );

        if (!subComment.getWriter().getId().equals(userDetails.getUser().getId())) {
            ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.FORBIDDEN).msg("해당 대댓글의 작성자가 아닙니다. 삭제 권한이 없습니다.").build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
        }

        subCommentRepository.delete(subComment);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("삭제 성공!").build();
        return ResponseEntity.ok(apiResponse);
    }
}
