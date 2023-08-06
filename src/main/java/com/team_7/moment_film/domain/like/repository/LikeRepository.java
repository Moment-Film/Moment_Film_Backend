package com.team_7.moment_film.domain.like.repository;

import com.team_7.moment_film.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
