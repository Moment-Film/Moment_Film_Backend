package com.team_7.moment_film.domain.bookmark.controller;

import com.team_7.moment_film.domain.bookmark.service.FilterBookmarkService;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filter")
@RequiredArgsConstructor
public class FilterBookmarkController {

    private final FilterBookmarkService bookMarkService;

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