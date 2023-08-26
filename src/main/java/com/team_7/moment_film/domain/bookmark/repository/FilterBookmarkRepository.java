package com.team_7.moment_film.domain.bookmark.repository;

import com.team_7.moment_film.domain.bookmark.entity.FilterBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilterBookmarkRepository extends JpaRepository<FilterBookmark,Long> {

    boolean existsByUserIdAndFilterId(Long userId, Long filterId);
    void deleteByUserIdAndFilterId(Long userId, Long filterId);
    List<FilterBookmark> findAllByUserId(Long UserId);
}