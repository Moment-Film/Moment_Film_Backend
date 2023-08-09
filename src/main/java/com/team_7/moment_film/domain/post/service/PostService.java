package com.team_7.moment_film.domain.post.service;

import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.comment.repository.CommentRepository;
import com.team_7.moment_film.domain.like.repository.LikeRepository;
import com.team_7.moment_film.domain.post.S3.service.S3Service;
import com.team_7.moment_film.domain.post.dto.PostRequestDto;
import com.team_7.moment_film.domain.post.dto.PostResponseDto;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.repository.PostRepository;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    //PostRequestDto requestDto
    // 생성
    @Transactional
    public CustomResponseEntity<PostResponseDto> createPost(PostRequestDto requestDto, MultipartFile image, UserDetailsImpl userDetails) {
        String imageUrl = s3Service.upload(image);
        log.info("file path = {}" , imageUrl);
        User user = getUserById(userDetails.getUser().getId());

        // 게시글 생성 및 저장
        Post savepost = Post.builder()
                .id(requestDto.getId())
                .title(requestDto.getTitle())
                .contents(requestDto.getContents())
                .image(imageUrl)
                .user(user)
                .build();

        postRepository.save(savepost);

        // 생성된 게시글 정보를 응답 DTO로 만들어 반환
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(savepost.getId())
                .title(savepost.getTitle())
                .contents(savepost.getContents())
                .image(savepost.getImage())
                .user(user)
                .isLiked(false)
                .build();
        return CustomResponseEntity.dataResponse(HttpStatus.CREATED,responseDto);
    }

    //삭제
    public CustomResponseEntity<?> deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("찾을 수 없습니다."));
        String imageurl = post.getImage();
        s3Service.delete(imageurl);
        postRepository.delete(post);
        return CustomResponseEntity.msgResponse(HttpStatus.OK,"삭제 성공!");
    }
    //상세조회
    public CustomResponseEntity<?> getPost(Long postId){
//        increaseViewCount(postId);
        Post post = postRepository.getPost(postId).orElseThrow(() -> new IllegalArgumentException("게시글 찾기 실패!"));
//        boolean isLiked = likeRepository.existsByPostIdAndUserId(postId,userDetails.getUser().getId());
//        List<CommentResponseDTO> commentResponseDTOList = commentRepository.findByPostId(postId);
        PostResponseDto responseDto = new PostResponseDto(post);
//        responseDto.setCommentResponseDTOList(commentResponseDTOList);
        return CustomResponseEntity.dataResponse(HttpStatus.OK,responseDto);
    }




//    //좋아요
//    public void increaseLikeCount(Long postId) {
//        Post post = postRepository.findById(postId).orElse(null);
//        if (post != null) {
//            post.setLikeCount(post.getLikeCount() + 1);
//            postRepository.save(post);
//        }
//    }
//
//    //조회수 증가
//    public void increaseViewCount(Long postId) {
//        Post post = postRepository.findById(postId).orElse(null);
//        if (post != null) {
//            post.setViewCount(post.getViewCount() + 1);
//            postRepository.save(post);
//        }
//    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. userId: " + userId));
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }
}