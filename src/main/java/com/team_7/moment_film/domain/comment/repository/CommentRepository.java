package com.team_7.moment_film.domain.comment.repository;

import com.team_7.moment_film.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> , CustomCommentRepository {
}
