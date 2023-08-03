package com.team_7.moment_film.domain.customframe.service;

import com.team_7.moment_film.domain.customframe.dto.FrameRequestDto;
import com.team_7.moment_film.domain.customframe.dto.FrameResponseDto;
import com.team_7.moment_film.domain.customframe.entity.Frame;
import com.team_7.moment_film.domain.customframe.repository.FrameRepository;
import com.team_7.moment_film.domain.user.User;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FrameService {

    private final FrameRepository frameRepository;
    public CustomResponseEntity<FrameResponseDto> createFrame(FrameRequestDto requestDto, User user) {
        Frame frame = new Frame(requestDto, user);
        frameRepository.save(frame);
        FrameResponseDto responseDto = FrameResponseDto.builder()
                .id(frame.getId())
                .frameName(frame.getFrameName())
                .build();
        return CustomResponseEntity.dataResponse(HttpStatus.CREATED,responseDto);
    }

    public List<FrameResponseDto> getAllFrame() {
        return frameRepository.findAll().stream().map(frame -> FrameResponseDto.builder()
                .id(frame.getId())
                .frameName(frame.getFrameName())
                .build()).collect(Collectors.toList());
    }

    public CustomResponseEntity<FrameResponseDto> selectFrame(Long frameId) {
        Frame frame = frameRepository.findById(frameId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 프레임입니다."));
        FrameResponseDto responseDto = FrameResponseDto.builder()
                .frameName(frame.getFrameName())
                .build();
        return CustomResponseEntity.dataResponse(HttpStatus.OK, responseDto);
    }
}
