package com.team_7.moment_film.domain.customfilter.repository;

import com.team_7.moment_film.domain.customfilter.entity.Filter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilterRepository extends JpaRepository<Filter,Long> {
}
