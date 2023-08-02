package com.team_7.moment_film.domain.post.service;

import com.team_7.moment_film.domain.post.S3.service.S3Service;
import com.team_7.moment_film.domain.post.dto.PostRequestDto;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.repository.PostRepository;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {


    private final PostRepository postRepository;
    private final S3Service s3Service;


    // 생성
    public CustomResponseEntity<?> createPost(PostRequestDto requestDto, MultipartFile image) {
        String imageUrl = s3Service.upload(image);
        Post savepost = postRepository.save(new Post(requestDto, imageUrl));
        log.info("생성 중 !", imageUrl, image);
        return new CustomResponseEntity<>(HttpStatus.CREATED, "생성되었습니다.", savepost);
    }

    //삭제
    public CustomResponseEntity<?> deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("찾을 수 없습니다."));
        String imageurl = post.getImage();
        s3Service.delete(imageurl);
        postRepository.delete(post);
        return new CustomResponseEntity<>(HttpStatus.OK, "삭제되었습니다.", post);
    }

//    public CustomResponseEntity<?> singlePost(Long postId){
//
//    }
}