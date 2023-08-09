package com.team_7.moment_film.domain.comment.controller;


import com.team_7.moment_film.domain.comment.dto.SubCommentRequestDTO;
import com.team_7.moment_film.domain.comment.dto.SubCommentResponseDTO;
import com.team_7.moment_film.domain.comment.service.SubCommentService;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/post/{postId}/comment/{commentId}/subcomment")
public class SubCommentController {


    private final SubCommentService subCommentService;


    //대댓글 작성
    @PostMapping
    public CustomResponseEntity<SubCommentResponseDTO> createSubComment(@PathVariable Long commentId,@RequestBody SubCommentRequestDTO requestDTO,
                                                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        return subCommentService.createSubComment(commentId,requestDTO, userDetails);
    }

    //대댓글 삭제
    @DeleteMapping("/{subcommentId}")
    public CustomResponseEntity<?> deleteSubComment(@PathVariable Long subcommentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return subCommentService.deleteSubComment(subcommentId, userDetails);
    }



}
