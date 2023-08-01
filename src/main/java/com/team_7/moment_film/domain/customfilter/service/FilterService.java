package com.team_7.moment_film.domain.customfilter.service;

import com.team_7.moment_film.domain.customfilter.dto.FilterRequestDto;
import com.team_7.moment_film.domain.customfilter.dto.FilterResponseDto;
import com.team_7.moment_film.domain.customfilter.entity.Filter;
import com.team_7.moment_film.domain.customfilter.repository.FilterRepository;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterService {
    //필터를 선택하지 않은 user는 기본값을 가짐

    private final FilterRepository filterRepository;
    public CustomResponseEntity<FilterResponseDto> createFilter(FilterRequestDto requestDto) {
        Filter filter = new Filter(requestDto); // user 추가 필요
        filterRepository.save(filter);
        FilterResponseDto responseDto = FilterResponseDto.builder()
                .id(filter.getId())
                .filterName(filter.getFilterName())
                .filterValue(filter.getFilterValue())
                .build();
        return CustomResponseEntity.dataResponse(HttpStatus.CREATED,responseDto);
    }

    public List<FilterResponseDto> getAllFilter() {
        return filterRepository.findAll().stream().map(filter -> FilterResponseDto.builder()
                .id(filter.getId())
                .filterName(filter.getFilterName())
                .filterValue(filter.getFilterValue())
                .build()).collect(Collectors.toList());
    }

    public CustomResponseEntity<FilterResponseDto> selectFilter(Long filterId) {
        Filter filter = findFilter(filterId);
        FilterResponseDto responseDto = FilterResponseDto.builder()
                .id(filter.getId())
                .filterName(filter.getFilterName())
                .filterValue(filter.getFilterValue())
                .build();
        return CustomResponseEntity.dataResponse(HttpStatus.OK,responseDto);
    }

    private Filter findFilter(Long filterId){
        return filterRepository.findById(filterId).orElseThrow(()->
                new IllegalArgumentException("존재하지 않는 필터입니다."));
    }
}
