package com.team_7.moment_film.domain.customfilter.service;

import com.team_7.moment_film.domain.customfilter.dto.FilterRequestDto;
import com.team_7.moment_film.domain.customfilter.dto.FilterResponseDto;
import com.team_7.moment_film.domain.customfilter.entity.Filter;
import com.team_7.moment_film.domain.customfilter.repository.FilterRepository;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilterService {

    private final FilterRepository filterRepository;

    public ResponseEntity<ApiResponse> createFilter(FilterRequestDto requestDto, User user) {
        if (requestDto.getBlur() == null && requestDto.getContrast() == null &&
                requestDto.getGrayscale() == null && requestDto.getSepia() == null) {
            throw new IllegalArgumentException("필터 값을 선택해주세요.");
        }

        Filter filter = new Filter(requestDto, user);
        filterRepository.save(filter);
        FilterResponseDto responseDto = FilterResponseDto.builder()
                .id(filter.getId())
                .filterName(filter.getFilterName())
                .blur(filter.getBlur())
                .contrast(filter.getContrast())
                .grayscale(filter.getGrayscale())
                .sepia(filter.getSepia())
                .build();
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.CREATED).data(responseDto).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    public ResponseEntity<ApiResponse> getAllFilter() {
        List<FilterResponseDto> filterList = filterRepository.findAll().stream().map(filter -> FilterResponseDto.builder()
                .id(filter.getId())
                .filterName(filter.getFilterName())
                .blur(filter.getBlur())
                .contrast(filter.getContrast())
                .grayscale(filter.getGrayscale())
                .sepia(filter.getSepia())
                .build()).collect(Collectors.toList());
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.CREATED).data(filterList).build();
        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<ApiResponse> selectFilter(Long filterId) {
        Filter filter = filterRepository.findById(filterId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 필터입니다."));
        FilterResponseDto responseDto = FilterResponseDto.builder()
                .id(filter.getId())
                .filterName(filter.getFilterName())
                .blur(filter.getBlur())
                .contrast(filter.getContrast())
                .grayscale(filter.getGrayscale())
                .sepia(filter.getSepia())
                .build();
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(responseDto).build();
        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<ApiResponse> deleteFilter(Long filterId, User user) {
        Filter filter = filterRepository.findById(filterId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 필터입니다."));
        if (!user.getId().equals(filter.getUser().getId())) {
            throw new IllegalArgumentException("직접 생성한 필터만 삭제 가능합니다.");
        }
        filterRepository.delete(filter);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("필터 삭제 완료").build();
        return ResponseEntity.ok(apiResponse);
    }
}
