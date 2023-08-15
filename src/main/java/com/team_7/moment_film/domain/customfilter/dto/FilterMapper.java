package com.team_7.moment_film.domain.customfilter.dto;

import com.team_7.moment_film.domain.customfilter.entity.Filter;
import com.team_7.moment_film.global.dto.GenericMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FilterMapper extends GenericMapper<FilterResponseDto, Filter> {
}