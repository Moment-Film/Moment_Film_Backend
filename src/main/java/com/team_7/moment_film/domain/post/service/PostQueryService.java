package com.team_7.moment_film.domain.post.service;

import com.team_7.moment_film.domain.post.dto.PostSliceRequest;
import com.team_7.moment_film.domain.post.dto.PostSliceResponse;
import com.team_7.moment_film.domain.post.repository.PostQueryRepository;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
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
    public CustomResponseEntity<List<PostSliceResponse>> getAll(PostSliceRequest request){
        return new CustomResponseEntity<>(HttpStatus.OK,"무한스크롤 전체조회",postQueryRepository.getSliceOfPost(request.id(), request.size()).stream()
                .map(PostSliceResponse::from)
                .collect(Collectors.toList()));
    }

    // 조회수(무한스크롤)
    public CustomResponseEntity<List<PostSliceResponse>> findAllOrderByViewCountDesc(PostSliceRequest request){
        return new CustomResponseEntity<>(HttpStatus.OK,"조회수순으로 조회",postQueryRepository.findAllOrderByViewCountDesc(request.id(), request.size()).stream()
                .map(PostSliceResponse::from)
                .collect(Collectors.toList()));
    }

    //좋아요 (무한스크롤)
    public CustomResponseEntity<List<PostSliceResponse>> findAllOrderByLikeCountDesc(PostSliceRequest request){
        return new CustomResponseEntity<>(HttpStatus.OK,"조회수순으로 조회",postQueryRepository.findAllOrderByLikeCountDesc(request.id(), request.size()).stream()
                .map(PostSliceResponse::from)
                .collect(Collectors.toList()));
    }

}
