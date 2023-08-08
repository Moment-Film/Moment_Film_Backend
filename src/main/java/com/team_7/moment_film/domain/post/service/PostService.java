package com.team_7.moment_film.domain.post.service;

import com.team_7.moment_film.domain.comment.dto.CommentRequestDTO;
import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.comment.entity.Comment;
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


    // 생성
    @Transactional
    public CustomResponseEntity<?> createPost(PostRequestDto requestDto, MultipartFile image,User user)  {
        String imageUrl = s3Service.upload(image);
        log.info("imageUrl = {}", imageUrl);

        Post post = Post.builder()
                .image(imageUrl)
                .user(user)
                .build();
        postRepository.save(post);

        PostResponseDto responseDto = PostResponseDto.builder()
                .id(post.getId())
                .image(post.getImage())
                .createdAt(post.getCreatedAt())
                .username(post.getUsername())
                .build();

        log.info("게시물 생성", user.getUsername());
        return CustomResponseEntity.dataResponse(HttpStatus.CREATED, responseDto);
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
    public CustomResponseEntity<?> getPost(Long postId, UserDetailsImpl userDetails){
        Post post = postRepository.getPost(postId).orElseThrow(() -> new IllegalArgumentException("게시글 찾기 실패!"));
        boolean isLiked = likeRepository.existsByPostIdAndUserId(postId,userDetails.getUser().getId());
        List<CommentResponseDTO> commentResponseDTOList = commentRepository.findByPostId(postId);
        PostResponseDto responseDto = new PostResponseDto(post,isLiked);
        responseDto.setCommentResponseDTOList(commentResponseDTOList);
        return CustomResponseEntity.dataResponse(HttpStatus.OK,responseDto);
    }






    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }
}