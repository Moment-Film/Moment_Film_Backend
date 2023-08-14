package com.team_7.moment_film.domain.customframe.controller;

import com.team_7.moment_film.domain.customframe.dto.FrameRequestDto;
import com.team_7.moment_film.domain.customframe.service.FrameService;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/frame")
@RequiredArgsConstructor
public class FrameController {

    private final FrameService frameService;

    //프레임 커스텀하기(등록하기)
    @PostMapping("")
    public ResponseEntity<ApiResponse> createFrame(@Valid @RequestPart(value = "name") FrameRequestDto requestDto,
                                                   @RequestPart(value = "frame", required = false) MultipartFile image,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails){
        return frameService.createFrame(requestDto, image, userDetails.getUser());
    }

    //유저들이 커스텀한 프레임 모두 조회
    @GetMapping("")
    public ResponseEntity<ApiResponse> getAllFrame() {
        return frameService.getAllFrame();
    }

    //커스텀 프레임 선택(적용)하기
    @PostMapping("/{frameId}")
    public ResponseEntity<ApiResponse> selectFrame(@PathVariable Long frameId) {
        return frameService.selectFrame(frameId);
    }

    //커스텀 프레임 지우기
    @DeleteMapping("/{frameId}")
    public ResponseEntity<ApiResponse> deleteFrame(@PathVariable Long frameId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return frameService.deleteFrame(frameId, userDetails.getUser());
    }

}