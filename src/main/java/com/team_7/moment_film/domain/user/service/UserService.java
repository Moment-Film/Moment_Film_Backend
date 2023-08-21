package com.team_7.moment_film.domain.user.service;

import com.team_7.moment_film.domain.follow.entity.Follow;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.repository.PostQueryRepository;
import com.team_7.moment_film.domain.user.dto.ProfileResponseDto;
import com.team_7.moment_film.domain.user.dto.SignupRequestDto;
import com.team_7.moment_film.domain.user.dto.UpdateRequestDto;
import com.team_7.moment_film.domain.user.dto.UserInfoDto;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.util.EncryptUtil;
import com.team_7.moment_film.global.util.JwtUtil;
import com.team_7.moment_film.global.util.RedisUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

@Slf4j(topic = "User Service")
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostQueryRepository postQueryRepository;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;
    private final EncryptUtil encryptUtil;

    // 회원가입
    public ResponseEntity<ApiResponse> signup(SignupRequestDto signupRequestDto) throws GeneralSecurityException, IOException {
        String email = signupRequestDto.getEmail();
        String username = signupRequestDto.getUsername();
        String phone = signupRequestDto.getPhone();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        log.info("phone 최초 평문 데이터 = " + phone);
        String encryptPhone = encryptUtil.encrypt(phone);
        log.info("암호화 및 인코딩 후 데이터 = " + encryptPhone);


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
                .phone(encryptPhone)
                .provider("momentFilm")
                .build();

        userRepository.save(user);

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("회원가입을 축하합니다!").build();
        return ResponseEntity.ok(apiResponse);
    }

    // 프로필 조회
    public ResponseEntity<ApiResponse> getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        // 내가 작성한 게시글 리스트
        List<Post> myPosts = postQueryRepository.getMyPosts(userId);
        myPosts.stream().map(post -> Post.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .image(post.getImage())
                .userId(post.getUserId())
                .username(post.getUsername())
                .build()).toList();

        // 내가 좋아요한 게시글 리스트
        List<Post> likedPosts = postQueryRepository.getLikedPosts(userId);
        likedPosts.stream().map(post -> Post.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .image(post.getImage())
                .userId(post.getUserId())
                .username(post.getUsername())
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

        // 프로필 정보가 담긴 DTO
        ProfileResponseDto responseDto = ProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .followerList(followers)
                .followingList(followings)
                .postList(myPosts)
                .postListCnt(myPosts.size())
                .likePosts(likedPosts)
                .build();

        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(responseDto).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 사용자 검색
    public ResponseEntity<ApiResponse> searchUser(String userKeyword) {
        if (userKeyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해주세요.");
        }
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(userRepository.searchUserByName(userKeyword)).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 팔로워 많은 순으로 사용자 조회
    public ResponseEntity<ApiResponse> getPopularUser() {
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(userRepository.getPopularUser()).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 개인 정보 조회
    public ResponseEntity<ApiResponse> getInfo(User user) {

        UserInfoDto userInfoDto = UserInfoDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .build();
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(userInfoDto).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 개인 정보 수정
    public ResponseEntity<ApiResponse> updateInfo(UpdateRequestDto requestDto, User user) {
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
                .provider(user.getProvider())
                .build();

        userRepository.save(updateUser);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("개인정보 수정 완료").build();
        return ResponseEntity.ok(apiResponse);
    }

    // 비밀번호 변경
    public ResponseEntity<ApiResponse> resetPassword(UpdateRequestDto requestDto, User user, String code) {
        if (!checkCode(user, code)) {
            throw new IllegalArgumentException("코드가 일치하지 않습니다.");
        }
        if (requestDto.getPassword() == null) {
            throw new IllegalArgumentException("새비밀번호를 입력해주세요.");
        }

        String newPassword = passwordEncoder.encode(requestDto.getPassword());

        User updateUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .password(newPassword)
                .provider(user.getProvider())
                .build();

        userRepository.save(updateUser);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("비밀번호 변경 완료").build();
        return ResponseEntity.ok(apiResponse);
    }

    // 회원탈퇴
    @Transactional
    public ResponseEntity<ApiResponse> withdrawal(User user, String accessToken) {
        String username = user.getUsername();
        if (user.getProvider().equals("google")) {
            username += "(google)";
        } else if (user.getProvider().equals("kakao")) {
            username += "(kakao)";
        }

        // redis에 저장된 refreshToken 삭제
        if (redisUtil.checkData(username)) {
            redisUtil.deleteData(username);
        }

        // 사용자가 제출한 accessToken 블랙리스트에 추가 및 TTL 설정(남은 시간)
        String accessTokenValue = jwtUtil.substringToken(accessToken);
        Date expiration = jwtUtil.getUserInfoFromToken(accessTokenValue).getExpiration();
        redisUtil.setData(accessTokenValue, "logout", expiration);

        // repository에서 해당 유저 제거
        userRepository.delete(user);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("회원 탈퇴 완료").build();
        return ResponseEntity.ok(apiResponse);
    }

    // 메일로 전송한 인증코드 일치 확인 메서드
    public Boolean checkCode(User user, String code) {
        String authCode = redisUtil.getData(user.getEmail());
        log.info("code=" + authCode);
        return authCode.equals(code);
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
