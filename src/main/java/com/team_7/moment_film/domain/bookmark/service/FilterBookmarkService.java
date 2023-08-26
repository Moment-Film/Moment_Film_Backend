package com.team_7.moment_film.domain.bookmark.service;

import com.team_7.moment_film.domain.customfilter.dto.FilterResponseDto;
import com.team_7.moment_film.domain.customfilter.entity.Filter;
import com.team_7.moment_film.domain.bookmark.entity.FilterBookmark;
import com.team_7.moment_film.domain.bookmark.repository.FilterBookmarkRepository;
import com.team_7.moment_film.domain.customfilter.repository.FilterRepository;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterBookmarkService {

    private final FilterBookmarkRepository bookMarkRepository;
    private final FilterRepository filterRepository;

    // 필터 북마크 추가/취소
    @Transactional
    public ResponseEntity<ApiResponse> bookMarkFilter(Long filterId, User user) {
        Filter filter = filterRepository.findById(filterId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 필터입니다."));
        boolean bookmarked = bookMarkRepository.existsByUserIdAndFilterId(user.getId(),filterId);

        // 북마크 없으면 추가
        if(!bookmarked){
            FilterBookmark bookMark = FilterBookmark.builder().user(user).filter(filter).build();
            bookMarkRepository.save(bookMark);

            ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("북마크 완료").build();
            return ResponseEntity.ok(apiResponse);
        } else{
            // 북마크 되어 있으면 삭제
            bookMarkRepository.deleteByUserIdAndFilterId(user.getId(),filterId);

            ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("북마크 취소 완료").build();
            return ResponseEntity.ok(apiResponse);
        }
    }

    // 북마크한 필터 리스트 조회
    public ResponseEntity<ApiResponse> getBookMarkFilter(User user) {

        List<FilterResponseDto> bookMarkList = bookMarkRepository.findAllByUserId(user.getId())
                .stream().map(filterBookMark-> FilterResponseDto.builder()
                        .id(filterBookMark.getFilter().getId())
                        .filterName(filterBookMark.getFilter().getFilterName()).build()).toList();

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(bookMarkList).build();
        return ResponseEntity.ok(apiResponse);
    }
}