package com.team_7.moment_film.domain.customfilter.repository;

import com.team_7.moment_film.domain.customfilter.entity.FilterBookMark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilterBookMarkRepository extends JpaRepository<FilterBookMark,Long> {

    boolean existsByUserIdAndFilterId(Long userId, Long filterId);
    void deleteByUserIdAndFilterId(Long userId, Long filterId);
    List<FilterBookMark> findAllByUserId(Long UserId);
}