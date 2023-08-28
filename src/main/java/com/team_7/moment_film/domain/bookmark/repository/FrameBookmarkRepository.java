package com.team_7.moment_film.domain.bookmark.repository;

import com.team_7.moment_film.domain.bookmark.entity.FrameBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrameBookmarkRepository extends JpaRepository<FrameBookmark,Long> {

    boolean existsByUserIdAndFrameId(Long userId, Long frameId);
    void deleteByUserIdAndFrameId(Long userId, Long frameId);
    List<FrameBookmark> findAllByUserId(Long UserId);
}
