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



    @PostMapping
    public CustomResponseEntity<?> createPost(@RequestPart(value = "data") PostRequestDto requestDto,
                                              @RequestPart(value = "imageFile") MultipartFile image,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return postService.createPost(requestDto,image,userDetails);
    }
    //삭제
    @DeleteMapping("/{postId}")
    public CustomResponseEntity<?> deletePost(@PathVariable Long postId,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.deletePost(postId,userDetails);
    }

    // 무한 스크롤 페이징
    @GetMapping()
    public CustomResponseEntity<List<PostSliceResponse>> getAll(PostSliceRequest request){
        return postQueryService.getAll(request);
    }

    // 조회수 순으로 조회
    @GetMapping("/view")
    public CustomResponseEntity<List<PostSliceResponse>> findAllOrderByViewCountAsc(PostSliceRequest request){
        return postQueryService.findAllOrderByViewCountDesc(request);
    }

    //좋아요 순으로 조회
    @GetMapping("/like")
    public CustomResponseEntity<List<PostSliceResponse>> findAllOrderByLikeCountAsc(PostSliceRequest request){
        return postQueryService.findAllOrderByLikeCountDesc(request);
    }



    //상세보기, @AuthenticationPrincipal UserDetailsImpl userDetails
    @GetMapping("/{postId}")
    public CustomResponseEntity<?> getPost(@PathVariable(value = "postId") Long postId){
        return postService.getPost(postId);
    }
}
