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
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SubCommentService {



    private final CommentRepository commentRepository;
    private final SubCommentRepository subCommentRepository;
    private final UserRepository userRepository;

    // 대댓글 생성
    public CustomResponseEntity<SubCommentResponseDTO> createSubComment(Long commentId,SubCommentRequestDTO requestDTO, UserDetailsImpl userDetails){
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않은 댓글입니다.")
        );
        Post post = comment.getPost();
        User writer = userDetails.getUser();
        SubComment subComment = SubComment.builder()
                .post(post)
                .writer(writer)
                .comment(comment)
                .content(requestDTO.getContent())
                .build();
        subCommentRepository.save(subComment);
        SubCommentResponseDTO responseDTO = SubCommentResponseDTO.builder()
                .id(subComment.getId())
                .commentId(comment.getId())
                .username(writer.getUsername())
                .content(subComment.getContent())
                .build();

        return CustomResponseEntity.dataResponse(HttpStatus.CREATED,responseDTO);
    }

    // 대댓글 삭제
    public CustomResponseEntity<?> deleteSubComment(Long subcommentId, UserDetailsImpl userDetails){
        SubComment subComment = subCommentRepository.findById(subcommentId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 대댓글입니다.")
        );

        User user = userRepository.findById(userDetails.getId()).orElseThrow(
                () -> new IllegalArgumentException("올바른 사용자가 아닙니다.")
        );

        if (!subComment.getWriter().getId().equals(userDetails.getUser().getId())) {
            return CustomResponseEntity.errorResponse(HttpStatus.FORBIDDEN, "해당 대댓글의 작성자가 아닙니다. 삭제 권한이 없습니다.");
        }

        subCommentRepository.delete(subComment);
        return CustomResponseEntity.msgResponse(HttpStatus.OK, "삭제 성공!");


    }


    //대댓글 조회
    public CustomResponseEntity<List<SubCommentResponseDTO>> getSubComment(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 댓글입니다.")
        );

        List<SubComment> subCommentList = subCommentRepository.findAllByCommentId(commentId);
        List<SubCommentResponseDTO> subCommentResponseDTOList = new ArrayList<>();
        for(SubComment subComment : subCommentList){
            subCommentResponseDTOList.add(
                    SubCommentResponseDTO.builder()
                            .id(subComment.getId())
                            .commentId(commentId)
                            .content(subComment.getContent())
                            .build()
            );

        }
        return CustomResponseEntity.dataResponse(HttpStatus.OK,subCommentResponseDTOList);
    }
}
