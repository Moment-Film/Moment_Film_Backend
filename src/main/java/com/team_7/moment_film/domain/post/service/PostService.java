package com.team_7.moment_film.domain.post.service;

import com.team_7.moment_film.domain.comment.dto.CommentResponseDTO;
import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.customfilter.entity.Filter;
import com.team_7.moment_film.domain.customfilter.repository.FilterRepository;
import com.team_7.moment_film.domain.customframe.entity.Frame;
import com.team_7.moment_film.domain.customframe.repository.FrameRepository;
import com.team_7.moment_film.domain.follow.entity.Follow;
import com.team_7.moment_film.domain.follow.repository.FollowRepository;
import com.team_7.moment_film.domain.like.entity.Like;
import com.team_7.moment_film.domain.like.repository.LikeRepository;
import com.team_7.moment_film.domain.post.dto.PostRequestDto;
import com.team_7.moment_film.domain.post.dto.PostResponseDto;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.repository.PostRepository;
import com.team_7.moment_film.domain.subcomment.dto.SubCommentResponseDTO;
import com.team_7.moment_film.domain.subcomment.entity.SubComment;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.security.UserDetailsImpl;
import com.team_7.moment_film.global.util.ClientUtil;
import com.team_7.moment_film.global.util.ViewCountUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final FilterRepository filterRepository;
    private final FrameRepository frameRepository;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;
    // 생성

    public ResponseEntity<ApiResponse> createPost(PostRequestDto requestDto, MultipartFile image, UserDetailsImpl userDetails) {
        Frame frame = frameRepository.findById(requestDto.getFrameId()).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않는 프레임 입니다.")
        );

        Filter filter = filterRepository.findById(requestDto.getFilterId()).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않는 필터입니다.")
        );

        String imageUrl = s3Service.upload(image, "post/");
        log.info("file path = {}", imageUrl);
        User user = getUserById(userDetails.getUser().getId());

        // 게시글 생성 및 저장
        Post savepost = Post.builder()
                .title(requestDto.getTitle())
                .contents(requestDto.getContents())
                .image(imageUrl)
                .user(user)
                .username(user.getUsername())
                .frame(frame)
                .filter(filter)
                .viewCount(0L)
                .build();
        postRepository.save(savepost);

        // 생성된 게시글 정보를 응답 DTO로 만들어 반환
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(savepost.getId())
                .title(savepost.getTitle())
                .contents(savepost.getContents())
                .image(s3Service.generateOriginalImageUrl(savepost.getImage()))
                .username(savepost.getUser().getUsername())
                .filterId(savepost.getFilter().getId())
                .frameId(savepost.getFrame().getId())
                .createdAt(savepost.getCreatedAtFormatted())
                .build();

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.CREATED).data(responseDto).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    //삭제
    public ResponseEntity<ApiResponse> deletePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("찾을 수 없습니다."));
        User user = getUserById(userDetails.getId());

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("해당 사용자가 아닙니다.");
        } else {
            String imageurl = post.getImage();
            s3Service.delete(imageurl);
            postRepository.delete(post);

            ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("삭제 성공!").build();
            return ResponseEntity.ok(apiResponse);
        }
    }


    //상세조회
    @Transactional
    public ResponseEntity<ApiResponse> getPost(Long postId, HttpServletRequest request) {
        Post post = postRepository.getPost(postId).orElseThrow(() -> new IllegalArgumentException("게시글 찾기 실패!"));
        increaseViewCount(postId);

        List<User> likeUser = post.getLikeList().stream().map(Like -> User.builder()
                .id(Like.getUser().getId())
                .build()
            ).collect(Collectors.toList());

        boolean isLiked = false;
        boolean isFollowed = false;

        // 사용자가 로그인한 경우에만 좋아요 및 팔로우 정보 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                isLiked = likeCheck(userDetails.getUser().getId(),postId);
                isFollowed = followerCheck(userDetails.getUser().getId());
            }
        }


        PostResponseDto responseDto = PostResponseDto.builder()
                .id(postId)
                .userId(post.getUser().getId())
                .username(post.getUsername())
                .title(post.getTitle())
                .contents(post.getContents())
                .image(s3Service.generateOriginalImageUrl(post.getImage()))
                .likeCount(post.getLikeList().size())
                .viewCount(post.getViewCount())
                .commentCount(post.getCommentList().size())
                .likeUserId(likeUser)
                .frameId(post.getFrame().getId())
                .frameName(post.getFrame().getFrameName())
                .filterId(post.getFilter().getId())
                .filterName(post.getFilter().getFilterName())
                .likeCheck(isLiked)
                .followerCheck(isFollowed)
                .commentList(getAllCommentsWithSubComments(post))
                .createdAt(post.getCreatedAtFormatted())
                .build();
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(responseDto).build();
        return ResponseEntity.ok(apiResponse);

    }

    // 댓글 대댓글 리스트 반환 메서드.
    public List<CommentResponseDTO> getAllCommentsWithSubComments(Post post) {
        List<CommentResponseDTO> allCommentsWithSubComments = new ArrayList<>();

        List<Comment> commentList = post.getCommentList();

        for (Comment comment : commentList) {
            CommentResponseDTO newComment = CommentResponseDTO.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .username(comment.getWriter().getUsername())
                    .userId(comment.getWriter().getId())
                    .createdAt(comment.getCreatedAtFormatted())
                    .build();
            List<SubCommentResponseDTO> newSubComments = new ArrayList<>();
            for (SubComment subComment : comment.getSubComments()) {
                SubCommentResponseDTO newSubComment = SubCommentResponseDTO.builder()
                        .id(subComment.getId())
                        .content(subComment.getContent())
                        .username(subComment.getWriter().getUsername())
                        .UserId(subComment.getWriter().getId())
                        .createdAt(subComment.getCreatedAtFormatted())
                        .build();
                newSubComments.add(newSubComment);
            }

            newComment.initializeSubComments(newSubComments);
            allCommentsWithSubComments.add(newComment);
        }

        return allCommentsWithSubComments;
    }

    public void increaseViewCount(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        String clientIp = ClientUtil.getRemoteIP();
        PostService.log.info("ip확인 4: " + clientIp);
        if(ViewCountUtil.canIncreaseViewCount(postId,clientIp)){
            post.incereaseViewCount(post);
            postRepository.save(post);
        }
    }


    public boolean likeCheck(Long userId,Long postId) {
        Optional<Like> like = likeRepository.findByUserIdAndPostId(userId,postId);

        return like.isPresent();
    }



    public boolean followerCheck(Long userId){
        Optional<Follow> follow = followRepository.findByFollowerId(userId);

        return follow.isPresent();
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