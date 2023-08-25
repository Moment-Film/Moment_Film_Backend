package com.team_7.moment_film.domain.post.repository;

import com.team_7.moment_film.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p join fetch p.user u where p.id = :postId")
    Optional<Post> getPost(@Param("postId") Long postId);
}
