package com.team_7.moment_film.domain.user.service;

import com.team_7.moment_film.domain.follow.entity.Follow;
import com.team_7.moment_film.domain.like.entity.Like;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.dto.PopularUserResponseDto;
import com.team_7.moment_film.domain.user.dto.ProfileResponseDto;
import com.team_7.moment_film.domain.user.dto.SearchResponseDto;
import com.team_7.moment_film.domain.user.dto.SignupRequestDto;
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

@Slf4j(topic = "User Service")
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        // 내가 작성한 게시글 리스트
        List<Post> postList = user.getPostList();
        List<Post> myPostList = postList.stream().map(post -> Post.builder()
                .id(post.getId())
                .image(post.getImage())
                .build()).toList();

        // 좋아요한 게시글 리스트
        List<Like> likeList = user.getLikeList();
        List<Post> likePosts = likeList.stream().map(like -> Post.builder()
                .id(like.getPost().getId())
                .image(like.getPost().getImage())
                .username(like.getUser().getUsername())
                .build()).toList();


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
                .postList(myPostList)
                .postListCnt(user.getPostList().size())
                .likePosts(likePosts)
                .build();

        return CustomResponseEntity.dataResponse(HttpStatus.OK, responseDto);
    }

    // 사용자 검색
    public CustomResponseEntity<List<SearchResponseDto>> searchUser(String userKeyword) {
        return CustomResponseEntity.dataResponse(HttpStatus.OK, userRepository.searchUserByName(userKeyword));
    }

    // 팔로워 많은 순으로 사용자 조회
    public CustomResponseEntity<List<PopularUserResponseDto>> getPopularUser() {
        return CustomResponseEntity.dataResponse(HttpStatus.OK, userRepository.getPopularUser());
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
