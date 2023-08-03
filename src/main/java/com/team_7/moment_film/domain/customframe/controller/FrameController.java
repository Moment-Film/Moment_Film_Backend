package com.team_7.moment_film.domain.customframe.controller;

import com.team_7.moment_film.domain.customframe.dto.FrameRequestDto;
import com.team_7.moment_film.domain.customframe.dto.FrameResponseDto;
import com.team_7.moment_film.domain.customframe.service.FrameService;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/frame")
@RequiredArgsConstructor
public class FrameController {

    private final FrameService frameService;

    //프레임 커스텀하기(등록하기)
    @PostMapping("")
    public CustomResponseEntity<FrameResponseDto> createFrame(@RequestBody FrameRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return frameService.createFrame(requestDto, userDetails.getUser());
    }

    //유저들이 커스텀한 프레임 모두 조회
    @GetMapping("")
    public List<FrameResponseDto> getAllFrame(){
        return frameService.getAllFrame();
    }

    //커스텀 프레임 선택(적용)하기
    @PostMapping("/{frameId}")
    public CustomResponseEntity<FrameResponseDto> selectFrame(@PathVariable Long frameId) {
        return frameService.selectFrame(frameId);
    }

}