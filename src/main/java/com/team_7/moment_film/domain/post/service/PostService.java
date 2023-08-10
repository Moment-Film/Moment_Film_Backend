package com.team_7.moment_film.domain.post.service;

import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.comment.entity.SubComment;
import com.team_7.moment_film.domain.comment.repository.CommentRepository;
import com.team_7.moment_film.domain.comment.repository.SubCommentRepository;
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
import org.mapstruct.control.MappingControl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
    private final SubCommentRepository subCommentRepository;


    // 생성
    @Transactional
    public CustomResponseEntity<PostResponseDto> createPost(PostRequestDto requestDto, MultipartFile image, UserDetailsImpl userDetails) {
            String imageUrl = s3Service.upload(image);
            log.info("file path = {}", imageUrl);
            User user = getUserById(userDetails.getUser().getId());

            // 게시글 생성 및 저장
            Post savepost = Post.builder()
                    .id(requestDto.getId())
                    .title(requestDto.getTitle())
                    .contents(requestDto.getContents())
                    .image(imageUrl)
                    .user(user)
                    .username(user.getUsername())
                    .commentCount(0)
                    .viewCount(0)
                    .likeCount(0)
                    .build();

            postRepository.save(savepost);
            // 생성된 게시글 정보를 응답 DTO로 만들어 반환
            PostResponseDto responseDto = PostResponseDto.builder()
                    .id(savepost.getId())
                    .title(savepost.getTitle())
                    .contents(savepost.getContents())
                    .image(savepost.getImage())
                    .user_Id(savepost.getUser().getId())
                    .username(savepost.getUsername())
                    .build();

            return CustomResponseEntity.dataResponse(HttpStatus.CREATED, responseDto);
    }

    //삭제
    public CustomResponseEntity<?> deletePost(Long postId,UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("찾을 수 없습니다."));
        User user = getUserById(userDetails.getId());

        if(!post.getUser().getId().equals(user.getId())){
            return CustomResponseEntity.errorResponse(HttpStatus.BAD_REQUEST,"해당 사용자가 아닙니다.!");
        }else {
            // 게시글 삭제 후 댓글과 대댓글들도 함께 삭제
            List<Comment> comments = post.getCommentList();
            for (Comment comment : comments) {
                List<SubComment> subComments = comment.getSubComments();
                subComments.forEach(subCommentRepository::delete);
                commentRepository.delete(comment);
            }
            String imageurl = post.getImage();
            s3Service.delete(imageurl);
            postRepository.delete(post);
            return CustomResponseEntity.msgResponse(HttpStatus.OK, "삭제 성공!");
        }
    }


    //상세조회
    public CustomResponseEntity<?> getPost(Long postId) {
            increaseViewCount(postId);
            Post post = postRepository.getPost(postId).orElseThrow(() -> new IllegalArgumentException("게시글 찾기 실패!"));
            PostResponseDto responseDto = PostResponseDto.builder()
                    .id(postId)
                    .username(post.getUser().getUsername())
                    .title(post.getTitle())
                    .contents(post.getContents())
                    .image(post.getImage())
                    .likeCount(post.getLikeCount())
                    .viewCount(post.getViewCount())
                    .commentCount(post.getCommentCount())
                    .commentList(post.getAllCommentsWithSubComments())
                    .createdAt(post.getCreatedAt())
                    .build();
            return CustomResponseEntity.dataResponse(HttpStatus.OK, responseDto);
    }




    // 조회수 증가
    public void increaseViewCount(Long postId) {
        increaseCount(postId, "viewCount");
    }

    // 카운터 증가 공통 메소드
    private void increaseCount(Long postId, String countField) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null) {
            Field field;
            try {
                field = Post.class.getDeclaredField(countField);
                field.setAccessible(true);
                Integer count = (Integer) field.get(post);
                field.set(post, count + 1);
                postRepository.save(post);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }





    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. userId: " + userId));
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }
}