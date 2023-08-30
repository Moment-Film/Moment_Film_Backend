package com.team_7.moment_film.domain.customframe.service;

import com.team_7.moment_film.domain.customframe.dto.FrameMapper;
import com.team_7.moment_film.domain.customframe.dto.FrameRequestDto;
import com.team_7.moment_film.domain.customframe.dto.FrameResponseDto;
import com.team_7.moment_film.domain.customframe.entity.Frame;
import com.team_7.moment_film.domain.customframe.repository.FrameRepository;
import com.team_7.moment_film.domain.post.service.S3Service;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.team_7.moment_film.global.dto.S3Prefix.FRAME;

@Service
@RequiredArgsConstructor
public class FrameService {

    private final FrameRepository frameRepository;
    private final S3Service s3Service;
    private final FrameMapper frameMapper;

    // 프레임 생성
    public ResponseEntity<ApiResponse> createFrame(FrameRequestDto requestDto, MultipartFile image, User user) {
        if(image==null && isNullOrBlank(requestDto.getHue()) &&
                isNullOrBlank(requestDto.getSaturation()) && isNullOrBlank(requestDto.getLightness())){
            throw new IllegalArgumentException("이미지나 값을 선택해주세요.");
        }
        // s3에 이미지 업로드
        String imageUrl = s3Service.upload(image, FRAME);

        Frame frame = new Frame(requestDto, imageUrl, user);
        frameRepository.save(frame);
        FrameResponseDto responseDto = frameMapper.toDto(frame);

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.CREATED).data(responseDto).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // 직접 만든 프레임 리스트 모두 조회
    public ResponseEntity<ApiResponse> getAllMyFrame(User user) {
        List<FrameResponseDto> frameList = frameRepository.findAllByUserId(user.getId()).stream().map(frame -> FrameResponseDto.builder()
                .id(frame.getId())
                .frameName(frame.getFrameName())
                .build()).toList();
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(frameList).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 프레임 선택
    public ResponseEntity<ApiResponse> selectFrame(Long frameId) {
        Frame frame = frameRepository.findById(frameId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 프레임입니다."));
        FrameResponseDto responseDto = frameMapper.toDto(frame);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(responseDto).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 프레임 삭제
    public ResponseEntity<ApiResponse> deleteFrame(Long frameId, User user) {
        Frame frame = frameRepository.findById(frameId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 프레임입니다."));
        if (!user.getId().equals(frame.getUser().getId())) {
            throw new IllegalArgumentException("직접 생성한 프레임만 삭제 가능합니다.");
        }
        frameRepository.delete(frame);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("프레임 삭제 완료").build();
        return ResponseEntity.ok(apiResponse);
    }

    // 프레임 값 하나 이상 선택했는지 확인
    private boolean isNullOrBlank(String value){
        return value == null || value.isBlank();
    }
}