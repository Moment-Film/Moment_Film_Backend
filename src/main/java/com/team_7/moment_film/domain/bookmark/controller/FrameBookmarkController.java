package com.team_7.moment_film.domain.bookmark.controller;

import com.team_7.moment_film.domain.bookmark.service.FrameBookmarkService;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/frame")
@RequiredArgsConstructor
public class FrameBookmarkController {

    private final FrameBookmarkService bookMarkService;

    // 북마크 추가/취소 기능
    @PostMapping("/{frameId}/bookmark")
    public ResponseEntity<ApiResponse> bookmarkFrame(@PathVariable Long frameId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return bookMarkService.bookmarkFrame(frameId, userDetails.getUser());
    }

    // 북마크한 프레임 조회 기능
    @GetMapping("/bookmark")
    public ResponseEntity<ApiResponse> getBookmarkFrame(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return bookMarkService.getBookmarkFrame(userDetails.getUser());
    }

}