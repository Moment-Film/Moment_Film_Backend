package com.team_7.moment_film.domain.post.repository;

import com.team_7.moment_film.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {


}
