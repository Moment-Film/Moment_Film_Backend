package com.team_7.moment_film.domain.post.service;

import com.team_7.moment_film.domain.post.dto.PostSliceLast;
import com.team_7.moment_film.domain.post.dto.PostSliceRequest;
import com.team_7.moment_film.domain.post.dto.PostSliceResponse;
import com.team_7.moment_film.domain.post.repository.PostQueryRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostQueryRepository postQueryRepository;


    // 전체조회(무한스크롤)
    public ResponseEntity<ApiResponse> getAll(PostSliceRequest request) {
        List<PostSliceResponse> postSliceResponseList = postQueryRepository
                .getSliceOfPost(request.id(), request.size(), request.page())
                .stream().map(PostSliceResponse::from)
                .collect(Collectors.toList());

        boolean isLastPage = postSliceResponseList.size() < request.size();
        PostSliceLast response = new PostSliceLast(postSliceResponseList, isLastPage);

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("무한스크롤 전체조회").data(response).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 조회수(무한스크롤)
    public ResponseEntity<ApiResponse> findAllOrderByViewCountDesc(PostSliceRequest request) {
        List<PostSliceResponse> postSliceResponseList = postQueryRepository
                .findAllOrderByViewCountDesc(request.id(), request.size(), request.page())
                .stream().map(PostSliceResponse::from)
                .collect(Collectors.toList());

        boolean isLastPage = postSliceResponseList.size() < request.size();
        PostSliceLast response = new PostSliceLast(postSliceResponseList, isLastPage);

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("조회수순으로 조회").data(response).build();
        return ResponseEntity.ok(apiResponse);

    }

    //좋아요 (무한스크롤)
    public ResponseEntity<ApiResponse> findAllOrderByLikeCountDesc(PostSliceRequest request) {
        List<PostSliceResponse> postSliceResponseList = postQueryRepository
                .findAllOrderByLikeCountDesc(request.id(), request.size(), request.page())
                .stream().map(PostSliceResponse::from)
                .collect(Collectors.toList());

        boolean isLastPage = postSliceResponseList.size() < request.size();
        PostSliceLast response = new PostSliceLast(postSliceResponseList, isLastPage);

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("좋아요순으로 조회").data(response).build();
        return ResponseEntity.ok(apiResponse);
    }

}
