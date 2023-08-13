package com.team_7.moment_film.domain.customfilter.controller;

import com.team_7.moment_film.domain.customfilter.dto.FilterRequestDto;
import com.team_7.moment_film.domain.customfilter.service.FilterService;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filter")
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;

    //필터 커스텀하기(등록하기)
    @PostMapping("")
    public ResponseEntity<ApiResponse> createFilter(@RequestBody FilterRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return filterService.createFilter(requestDto, userDetails.getUser());
    }

    //유저들이 커스텀한 필터 모두 조회
    @GetMapping("")
    public ResponseEntity<ApiResponse> getAllFilter() {
        return filterService.getAllFilter();
    }

    //커스텀 필터 선택(적용)하기
    @PostMapping("/{filterId}")
    public ResponseEntity<ApiResponse> selectFilter(@PathVariable Long filterId) {
        return filterService.selectFilter(filterId);
    }

    //커스텀 필터 지우기
    @DeleteMapping("/{filterId}")
    public ResponseEntity<ApiResponse> deleteFilter(@PathVariable Long filterId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return filterService.deleteFilter(filterId, userDetails.getUser());
    }

}