package com.team_7.moment_film.domain.customframe.repository;

import com.team_7.moment_film.domain.customframe.entity.Frame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrameRepository extends JpaRepository<Frame,Long> {
    List<Frame> findAllByUserId(Long UserId);
}