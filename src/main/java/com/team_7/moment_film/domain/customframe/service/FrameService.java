package com.team_7.moment_film.domain.customframe.service;

import com.team_7.moment_film.domain.customframe.dto.FrameRequestDto;
import com.team_7.moment_film.domain.customframe.dto.FrameResponseDto;
import com.team_7.moment_film.domain.customframe.entity.Frame;
import com.team_7.moment_film.domain.customframe.repository.FrameRepository;
import com.team_7.moment_film.domain.post.S3.service.S3Service;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FrameService {

    private final FrameRepository frameRepository;
    private final S3Service s3Service;


    public CustomResponseEntity<FrameResponseDto> createFrame(FrameRequestDto requestDto, MultipartFile image, User user) {
        String imageUrl = s3Service.upload(image);
        Frame frame = new Frame(requestDto, imageUrl, user);
        frameRepository.save(frame);
        FrameResponseDto responseDto = FrameResponseDto.builder()
                .id(frame.getId())
                .frameName(frame.getFrameName())
                .image(frame.getImage())
                .build();
        return CustomResponseEntity.dataResponse(HttpStatus.CREATED,responseDto);
    }

    public CustomResponseEntity<List<FrameResponseDto>> getAllFrame() {
        List<FrameResponseDto> frameList = frameRepository.findAll().stream().map(frame -> FrameResponseDto.builder()
                .id(frame.getId())
                .frameName(frame.getFrameName())
                .image(frame.getImage())
                .build()).collect(Collectors.toList());
        return CustomResponseEntity.dataResponse(HttpStatus.OK,frameList);
    }

    public CustomResponseEntity<FrameResponseDto> selectFrame(Long frameId) {
        Frame frame = frameRepository.findById(frameId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 프레임입니다."));
        FrameResponseDto responseDto = FrameResponseDto.builder()
                .id(frame.getId())
                .frameName(frame.getFrameName())
                .image(frame.getImage())
                .build();
        return CustomResponseEntity.dataResponse(HttpStatus.OK, responseDto);
    }

    public CustomResponseEntity<String> deleteFrame(Long frameId, User user) {
        Frame frame = frameRepository.findById(frameId).orElseThrow(()->
                new EntityNotFoundException("존재하지 않는 프레임입니다."));
        if(!user.getId().equals(frame.getUser().getId())){
            throw new IllegalArgumentException("직접 생성한 프레임만 삭제 가능합니다.");
        }
        frameRepository.delete(frame);
        return CustomResponseEntity.msgResponse(HttpStatus.OK,"프레임 삭제 완료");
    }
}
