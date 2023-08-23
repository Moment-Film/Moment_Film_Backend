package com.team_7.moment_film.domain.customframe.dto;

import com.team_7.moment_film.domain.customframe.entity.Frame;
import com.team_7.moment_film.global.dto.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FrameMapper extends GenericMapper<FrameResponseDto, Frame> {
}
