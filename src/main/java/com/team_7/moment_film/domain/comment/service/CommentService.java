package com.team_7.moment_film.domain.comment.service;


import com.team_7.moment_film.domain.comment.dto.CommentRequestDTO;
import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.comment.entity.SubComment;
import com.team_7.moment_film.domain.comment.repository.CommentRepository;
import com.team_7.moment_film.domain.comment.repository.SubCommentRepository;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.repository.PostRepository;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postrepository;
    private final UserRepository userRepository;
    private final SubCommentRepository subCommentRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);


    //댓글 작성 메소드
    public CustomResponseEntity<CommentResponseDTO> createComment(Long postId, CommentRequestDTO requestDTO, UserDetailsImpl userDetails) {
        try {
            Post post = postrepository.findById(postId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않은 게시글 입니다.")
            );
            User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않은 사용자 입니다.")
            );
            User writer = userDetails.getUser();

            Comment comment = Comment.builder()
                    .post(post)
                    .writer(writer)
                    .content(requestDTO.getContent())
                    .build();
            commentRepository.save(comment);
            CommentResponseDTO responseDTO = CommentResponseDTO.builder()
                    .id(comment.getId())
                    .username(writer.getUsername())
                    .content(comment.getContent())
                    .build();
            return CustomResponseEntity.dataResponse(HttpStatus.CREATED, responseDTO);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException occurred: {}", e.getMessage(), e);
            return CustomResponseEntity.errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred: " + e.getMessage());
            return CustomResponseEntity.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    //댓글 조회 메서드
    public CustomResponseEntity<List<CommentResponseDTO>> getAllComment(Long postId){
        Post post = postrepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않은 게시글 입니다.")
        );

        List<Comment> commentList = commentRepository.findAllByPostId(postId);
        List<CommentResponseDTO> commentResponseDTOList = new ArrayList<>();
        for(Comment comment : commentList){
            commentResponseDTOList.add(
                    CommentResponseDTO.builder()
                            .id(comment.getId())
                            .postId(comment.getPost().getId())
                            .content(comment.getContent())
                            .build()
            );
        }
        return CustomResponseEntity.dataResponse(HttpStatus.OK,commentResponseDTOList);
    }

    public CustomResponseEntity<Comment> deleteComment(Long commentId, UserDetailsImpl userDetails){
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않은 댓글입니다.")
        );

        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                ()-> new IllegalArgumentException("올바른 사용자가 아닙니다.")
        );
        if(!comment.getWriter().getId().equals(user.getId())){
            throw new IllegalArgumentException("해당 사용자가 아닙니다.");
        }else {
            // 댓글에 속한 대댓글들도 삭제
            List<SubComment> subComments = comment.getSubComments();
            subComments.forEach(subCommentRepository::delete);

            commentRepository.delete(comment);
            return CustomResponseEntity.msgResponse(HttpStatus.OK, "삭제 성공!");
        }
    }





}
