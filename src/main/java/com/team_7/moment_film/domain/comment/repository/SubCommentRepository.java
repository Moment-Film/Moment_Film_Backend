package com.team_7.moment_film.domain.comment.repository;

import com.team_7.moment_film.domain.comment.entity.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCommentRepository extends JpaRepository<SubComment, Long> {

    List<SubComment> findAllByCommentId(Long commentId);
}
