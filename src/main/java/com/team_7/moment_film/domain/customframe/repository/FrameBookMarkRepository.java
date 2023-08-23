package com.team_7.moment_film.domain.customframe.repository;

import com.team_7.moment_film.domain.customframe.entity.FrameBookMark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrameBookMarkRepository extends JpaRepository<FrameBookMark,Long> {

    boolean existsByUserIdAndFrameId(Long userId, Long frameId);
    void deleteByUserIdAndFrameId(Long userId, Long frameId);
    List<FrameBookMark> findAllByUserId(Long UserId);
}
