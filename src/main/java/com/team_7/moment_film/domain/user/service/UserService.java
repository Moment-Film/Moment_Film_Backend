package com.team_7.moment_film.domain.user.service;

import com.team_7.moment_film.domain.follow.entity.Follow;
import com.team_7.moment_film.domain.like.repository.LikeRepository;
import com.team_7.moment_film.domain.post.entity.TempPost;
import com.team_7.moment_film.domain.post.repository.PostQueryRepository;
import com.team_7.moment_film.domain.post.repository.PostRepository;
import com.team_7.moment_film.domain.user.dto.*;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "User Service")
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostQueryRepository postQueryRepository;

    // 회원가입 비즈니스 로직
    public CustomResponseEntity<String> signup(SignupRequestDto signupRequestDto) {
        String email = signupRequestDto.getEmail();
        String username = signupRequestDto.getUsername();
        String phone = signupRequestDto.getPhone();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        if (checkEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (checkUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 username 입니다.");
        }
        if (checkPhone(phone)) {
            throw new IllegalArgumentException("이미 존재하는 휴대폰 번호입니다.");
        }

        User user = User.builder()
                .email(email)
                .username(username)
                .password(password)
                .phone(phone)
                .isKakao(false)
                .build();

        userRepository.save(user);

        return CustomResponseEntity.msgResponse(HttpStatus.OK, "회원가입을 축하합니다!");
    }

    // 프로필 조회
    public CustomResponseEntity<ProfileResponseDto> getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

//         내가 작성한 게시글 리스트 (1번 방법 : userId로 PostRepository에서 게시글 리스트 조회)
//        List<Post> postList = postRepository.findByUserId(user.getId());
//        List<Post> myPostList = postList.stream().map(post -> Post.builder()
//                .id(post.getId())
//                .title(post.getTitle())
//                .image(post.getImage())
//                .contents(post.getContents())
//                .build()).toList();

//        내가 작성한 게시글 리스트 (2번 방법 : User Entity에서 양방향 참조로 연관된 PostList를 Getter로 조회) ✨
//        List<Post> postList = user1.getPostList().stream().map(post -> Post.builder()
//                .id(post.getId())
//                .title(post.getTitle())
//                .contents(post.getContents())
//                .image(post.getImage())
//                .build())
//                .toList();

        // 내가 작성한 게시글 리스트 테스트 (임시 Entity: Post Entity에서 일부 필드만 가져왔을 때 연관된 댓글/대댓글 필드도 다가져와짐)
        List<TempPost> postList = postQueryRepository.getMyPosts(userId);
        postList.stream().map(tempPost -> TempPost.builder()
                .id(tempPost.getId())
                .title(tempPost.getTitle())
                .contents(tempPost.getContents())
                .image(tempPost.getImage())
                .build())
                .collect(Collectors.toList());

//         좋아요한 게시글 리스트 (원본)
//        List<Like> likeList = likeRepository.findByUserId(user.getId());
//        List<Post> likePosts = likeList.stream().map(like -> Post.builder()
//                .id(like.getPost().getId())
//                .image(like.getPost().getImage())
//                .username(like.getUser().getUsername())
//                .build()).toList();
        List<TempPost> likedPostList = postQueryRepository.getLikedPosts(userId);
        likedPostList.stream().map(tempPost -> TempPost.builder()
                .id(tempPost.getId())
                .title(tempPost.getTitle())
                .contents(tempPost.getContents())
                .image(tempPost.getImage())
                .userId(tempPost.getUserId())
                .username(tempPost.getUsername())
                .build())
                .collect(Collectors.toList());


        // 조회한 유저의 팔로잉 리스트
        List<Follow> followingList = user.getFollowerList();
        List<User> followings = followingList.stream().map(follow -> User.builder()
                .id(follow.getFollowing().getId())
                .username(follow.getFollowing().getUsername())
                .build()).toList();


        // 조회한 유저의 팔로워 리스트
        List<Follow> followerList = user.getFollowingList();
        List<User> followers = followerList.stream().map(follow -> User.builder()
                .id(follow.getFollower().getId())
                .username(follow.getFollower().getUsername())
                .build()).toList();

        // 프로필 정보가 담긴 DTO 빌드
        ProfileResponseDto responseDto = ProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .followerList(followers)
                .followingList(followings)
                .postList(postList)
                .postListCnt(postList.size())
                .likePosts(likedPostList)
                .build();

        return CustomResponseEntity.dataResponse(HttpStatus.OK, responseDto);
    }

    // 사용자 검색
    public CustomResponseEntity<List<SearchResponseDto>> searchUser(String userKeyword) {
        if(userKeyword.isBlank()){
            throw new IllegalArgumentException("검색어를 입력해주세요.");
        }
        return CustomResponseEntity.dataResponse(HttpStatus.OK, userRepository.searchUserByName(userKeyword));
    }

    // 팔로워 많은 순으로 사용자 조회
    public CustomResponseEntity<List<PopularUserResponseDto>> getPopularUser() {
        return CustomResponseEntity.dataResponse(HttpStatus.OK, userRepository.getPopularUser());
    }

    // 개인 정보 조회
    public CustomResponseEntity<UserInfoDto> getInfo(User user) {

        UserInfoDto userInfoDto = UserInfoDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .build();
        return CustomResponseEntity.dataResponse(HttpStatus.OK, userInfoDto);
    }

    // 개인 정보 수정
    public CustomResponseEntity<String> updateInfo(UpdateRequestDto requestDto, User user) {
        // username, phone 정보만 수정 가능
        if (requestDto.getUsername() == null && requestDto.getPhone() == null) {
            throw new IllegalArgumentException("이름과 휴대폰 번호만 수정 가능합니다.");
        }

        User updateUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(requestDto.getUsername() != null ? requestDto.getUsername() : user.getUsername())
                .phone(requestDto.getPhone() != null ? requestDto.getPhone() : user.getPhone())
                .password(user.getPassword())
                .isKakao(user.isKakao())
                .build();

        userRepository.save(updateUser);
        return CustomResponseEntity.msgResponse(HttpStatus.OK,"개인정보 수정 완료");
    }

    // 이메일 중복 검사 메서드
    private boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Username 중복 검사 메서드
    private boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // 휴대폰 번호 중복 검사 메서드
    private boolean checkPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

}
