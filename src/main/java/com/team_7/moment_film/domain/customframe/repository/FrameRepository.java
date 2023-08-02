package com.team_7.moment_film.domain.customframe.repository;

import com.team_7.moment_film.domain.customframe.entity.Frame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrameRepository extends JpaRepository<Frame,Long> {
}