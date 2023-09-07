package com.team_7.moment_film.domain.bookmark.service;

import com.team_7.moment_film.domain.customframe.dto.FrameResponseDto;
import com.team_7.moment_film.domain.customframe.entity.Frame;
import com.team_7.moment_film.domain.bookmark.entity.FrameBookmark;
import com.team_7.moment_film.domain.bookmark.repository.FrameBookmarkRepository;
import com.team_7.moment_film.domain.customframe.repository.FrameRepository;
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
public class FrameBookmarkService {

    private final FrameBookmarkRepository bookMarkRepository;
    private final FrameRepository frameRepository;

    // 프레임 북마크 추가/취소
    @Transactional
    public ResponseEntity<ApiResponse> bookmarkFrame(Long frameId, User user) {
        Frame frame = frameRepository.findById(frameId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 프레임입니다."));
        boolean bookmarked = bookMarkRepository.existsByUserIdAndFrameId(user.getId(),frameId);

        // 북마크 없으면 추가
        if(!bookmarked){
            FrameBookmark bookMark = FrameBookmark.builder().user(user).frame(frame).build();
            bookMarkRepository.save(bookMark);

            ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("북마크 완료").build();
            return ResponseEntity.ok(apiResponse);
        } else{
            // 북마크 되어 있으면 삭제
            bookMarkRepository.deleteByUserIdAndFrameId(user.getId(),frameId);

            ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("북마크 취소 완료").build();
            return ResponseEntity.ok(apiResponse);
        }
    }

    // 북마크한 프레임 리스트 조회
    public ResponseEntity<ApiResponse> getBookmarkFrame(User user) {

        List<FrameResponseDto> bookMarkList = bookMarkRepository.findAllByUserId(user.getId())
                .stream().map(frameBookMark-> FrameResponseDto.builder()
                        .id(frameBookMark.getFrame().getId())
                        .frameName(frameBookMark.getFrame().getFrameName()).build()).toList();

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(bookMarkList).build();
        return ResponseEntity.ok(apiResponse);
    }
}