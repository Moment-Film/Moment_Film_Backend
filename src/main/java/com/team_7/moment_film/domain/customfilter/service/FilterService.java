package com.team_7.moment_film.domain.customfilter.service;

import com.team_7.moment_film.domain.customfilter.dto.FilterRequestDto;
import com.team_7.moment_film.domain.customfilter.dto.FilterResponseDto;
import com.team_7.moment_film.domain.customfilter.entity.Filter;
import com.team_7.moment_film.domain.customfilter.repository.FilterRepository;
import com.team_7.moment_film.domain.user.User;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilterService {

    private final FilterRepository filterRepository;

    public CustomResponseEntity<FilterResponseDto> createFilter(FilterRequestDto requestDto, User user) {
        Filter filter = new Filter(requestDto, user);
        filterRepository.save(filter);
        FilterResponseDto responseDto = FilterResponseDto.builder()
                .id(filter.getId())
                .blur(filter.getBlur())
                .contrast(filter.getContrast())
                .grayscale(filter.getGrayscale())
                .sepia(filter.getSepia())
                .build();
        return CustomResponseEntity.dataResponse(HttpStatus.CREATED,responseDto);
    }

    public List<FilterResponseDto> getAllFilter() {
        return filterRepository.findAll().stream().map(filter -> FilterResponseDto.builder()
                .id(filter.getId())
                .blur(filter.getBlur())
                .contrast(filter.getContrast())
                .grayscale(filter.getGrayscale())
                .sepia(filter.getSepia())
                .build()).collect(Collectors.toList());
    }

    public CustomResponseEntity<FilterResponseDto> selectFilter(Long filterId) {
        Filter filter = filterRepository.findById(filterId).orElseThrow(()->
                new IllegalArgumentException("존재하지 않는 필터입니다."));
        FilterResponseDto responseDto = FilterResponseDto.builder()
                .blur(filter.getBlur())
                .contrast(filter.getContrast())
                .grayscale(filter.getGrayscale())
                .sepia(filter.getSepia())
                .build();
        return CustomResponseEntity.dataResponse(HttpStatus.OK,responseDto);
    }
}