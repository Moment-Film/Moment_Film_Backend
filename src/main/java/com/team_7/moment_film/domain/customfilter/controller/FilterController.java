package com.team_7.moment_film.domain.customfilter.controller;

import com.team_7.moment_film.domain.customfilter.dto.FilterRequestDto;
import com.team_7.moment_film.domain.customfilter.service.FilterBookMarkService;
import com.team_7.moment_film.domain.customfilter.service.FilterService;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filter")
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;
    private final FilterBookMarkService bookMarkService;

    // 필터 커스텀(등록) 기능
    @PostMapping("")
    public ResponseEntity<ApiResponse> createFilter(@Valid @RequestBody FilterRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return filterService.createFilter(requestDto, userDetails.getUser());
    }

    // 내가 커스텀한 필터 모두 조회 기능
    @GetMapping("")
    public ResponseEntity<ApiResponse> getAllMyFilter(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return filterService.getAllMyFilter(userDetails.getUser());
    }

    // 커스텀 필터 선택(적용) 기능
    @PostMapping("/{filterId}")
    public ResponseEntity<ApiResponse> selectFilter(@PathVariable Long filterId) {
        return filterService.selectFilter(filterId);
    }

    // 커스텀 필터 삭제 기능
    @DeleteMapping("/{filterId}")
    public ResponseEntity<ApiResponse> deleteFilter(@PathVariable Long filterId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return filterService.deleteFilter(filterId, userDetails.getUser());
    }

    // 북마크 추가/취소 기능
    @PostMapping("/{filterId}/bookmark")
    public ResponseEntity<ApiResponse> BookMarkFilter(@PathVariable Long filterId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return bookMarkService.bookMarkFilter(filterId, userDetails.getUser());
    }

    // 북마크한 필터 조회 기능
    @GetMapping("/bookmark")
    public ResponseEntity<ApiResponse> GetBookMarkFilter(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return bookMarkService.getBookMarkFilter(userDetails.getUser());
    }

}