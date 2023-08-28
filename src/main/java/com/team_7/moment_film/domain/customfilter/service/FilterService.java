package com.team_7.moment_film.domain.customfilter.service;

import com.team_7.moment_film.domain.customfilter.dto.FilterMapper;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class FilterService {

    private final FilterRepository filterRepository;
    private final FilterMapper filterMapper;

    // 필터 생성
    public ResponseEntity<ApiResponse> createFilter(FilterRequestDto requestDto, User user) {
        if ( isNullOrBlank(requestDto.getBlur()) && isNullOrBlank(requestDto.getBrightness()) &&
                isNullOrBlank(requestDto.getContrast()) && isNullOrBlank(requestDto.getSaturate()) &&
                isNullOrBlank(requestDto.getSepia()) ) {
            throw new IllegalArgumentException("필터 값을 최소 하나 이상 선택해주세요.");
        }
        Filter filter = new Filter(requestDto, user);
        filterRepository.save(filter);
        FilterResponseDto responseDto = filterMapper.toDto(filter);

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.CREATED).data(responseDto).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // 직접 만든 필터 리스트 모두 조회
    public ResponseEntity<ApiResponse> getAllMyFilter(User user) {
        List<FilterResponseDto> filterList = filterRepository.findAllByUserId(user.getId()).stream().map(filter -> FilterResponseDto.builder()
                .id(filter.getId())
                .filterName(filter.getFilterName())
                .build()).toList();
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(filterList).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 필터 선택
    public ResponseEntity<ApiResponse> selectFilter(Long filterId) {
        Filter filter = filterRepository.findById(filterId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 필터입니다."));
        FilterResponseDto responseDto = filterMapper.toDto(filter);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(responseDto).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 필터 삭제
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

    // 필터 값 하나 이상 선택했는지 확인
    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

}