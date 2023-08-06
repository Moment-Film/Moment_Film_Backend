package com.team_7.moment_film.domain.post.controller;


import com.team_7.moment_film.domain.post.dto.PostRequestDto;
import com.team_7.moment_film.domain.post.dto.PostResponseDto;
import com.team_7.moment_film.domain.post.dto.PostSliceRequest;
import com.team_7.moment_film.domain.post.dto.PostSliceResponse;
import com.team_7.moment_film.domain.post.service.PostQueryService;
import com.team_7.moment_film.domain.post.service.PostService;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/post")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final PostQueryService postQueryService;

    //생성
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public CustomResponseEntity<?> createPost(@RequestPart(value = "data", required = false)PostRequestDto requestDto,
                                              @RequestPart(value = "imageFile", required = false) MultipartFile image,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails
                                                ){
        return postService.createPost(requestDto,image,userDetails.getUser());
    }
    //삭제
    @DeleteMapping("/{postId}")
    public CustomResponseEntity<?> deletePost(@PathVariable Long postId){
        return postService.deletePost(postId);
    }

    // 무한 스크롤 페이징
    @GetMapping()
    public CustomResponseEntity<List<PostSliceResponse>> getAll(PostSliceRequest request){
        return postQueryService.getAll(request);
    }

    //상세보기
    @GetMapping("/{postId}")
    public CustomResponseEntity<?> getPost(@PathVariable(value = "postId") Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.getPost(postId,userDetails);
    }
}
