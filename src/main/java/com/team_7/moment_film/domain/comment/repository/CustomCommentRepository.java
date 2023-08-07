package com.team_7.moment_film.domain.comment.repository;

import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.comment.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CustomCommentRepository {
    List<CommentResponseDTO> findByPostId(Long id);

    Optional<Comment> findCommentByIdWithParent(Long id);
}
