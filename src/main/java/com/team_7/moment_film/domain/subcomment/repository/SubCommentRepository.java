package com.team_7.moment_film.domain.subcomment.repository;

import com.team_7.moment_film.domain.subcomment.entity.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
}
