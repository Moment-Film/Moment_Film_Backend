package com.team_7.moment_film.domain.user.service;

import com.team_7.moment_film.domain.follow.entity.Follow;
import com.team_7.moment_film.domain.follow.repository.FollowRepository;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.repository.PostQueryRepository;
import com.team_7.moment_film.domain.post.service.S3Service;
import com.team_7.moment_film.domain.user.dto.*;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.point.PointCategory;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.ApiResponse;
import com.team_7.moment_film.global.util.EncryptUtil;
import com.team_7.moment_film.global.util.JwtUtil;
import com.team_7.moment_film.global.util.RedisUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.team_7.moment_film.global.dto.S3Prefix.PROFILE;

@Slf4j(topic = "User Service")
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostQueryRepository postQueryRepository;
    private final FollowRepository followRepository;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;
    private final EncryptUtil encryptUtil;
    private final S3Service s3Service;

    // 회원가입
    public ResponseEntity<ApiResponse> signup(SignupRequestDto signupRequestDto) throws GeneralSecurityException, IOException {
        String email = signupRequestDto.getEmail();
        String username = signupRequestDto.getUsername();
        String phone = signupRequestDto.getPhone();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        log.info("phone 최초 평문 데이터 = " + phone);
        String encryptPhone = encryptUtil.encrypt(phone);
        log.info("암호화 및 인코딩 후 데이터 = " + encryptPhone);
        String phoneHash = sha256Hashing(phone);
        log.info("phone Hashing 후 데이터 = "+ phoneHash);

        if (checkEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (checkUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 username 입니다.");
        }
        if (checkPhone(phoneHash)) {
            throw new IllegalArgumentException("이미 존재하는 휴대폰 번호입니다.");
        }

        User user = User.builder()
                .email(email)
                .username(username)
                .password(password)
                .phone(encryptPhone)
                .phoneHash(phoneHash)
                .provider("momentFilm")
                .point(1000L)
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
                .userImage(post.getUserImage())
                .build()).toList();

        // 내가 좋아요한 게시글 리스트!
        List<Post> likedPosts = postQueryRepository.getLikedPosts(userId);
        likedPosts.stream().map(post -> Post.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .image(post.getImage())
                .userId(post.getUserId())
                .username(post.getUsername())
                .userImage(post.getUserImage())
                .build()).toList();

        // 조회한 유저의 팔로잉 리스트
        List<Follow> followingList = followRepository.findAllByFollowerId(user.getId());
        List<User> followings = followingList.stream().map(follow -> User.builder()
                .id(follow.getFollowing().getId())
                .username(follow.getFollowing().getUsername())
                .build()).toList();

        // 조회한 유저의 팔로워 리스트
        List<Follow> followerList = followRepository.findAllByFollowingId(user.getId());
        List<User> followers = followerList.stream().map(follow -> User.builder()
                .id(follow.getFollower().getId())
                .username(follow.getFollower().getUsername())
                .build()).toList();

        // 프로필 정보가 담긴 DTO
        ProfileResponseDto responseDto = ProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .image(user.getImage())
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
    public ResponseEntity<ApiResponse> searchUser(String userKeyword, Pageable pageable) {
        if (userKeyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해주세요.");
        }
        // page size 파라미터로 수정못하도록 고정
        Pageable defaultPageable = PageRequest.of(pageable.getPageNumber(),6);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(userRepository.searchUserByName(userKeyword, defaultPageable)).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 팔로워 순으로 사용자 조회
    public ResponseEntity<ApiResponse> getPopularUser() {
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(userRepository.getPopularUser()).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 개인 정보 조회
    public ResponseEntity<ApiResponse> getInfo(User user) throws GeneralSecurityException, IOException {
        // 휴대폰 번호 복호화 후 반환
        String phone = encryptUtil.decrypt(user.getPhone());

        UserInfoDto userInfoDto = UserInfoDto.builder()
                .username(user.getUsername())
                .image(user.getImage())
                .email(user.getEmail())
                .phone(phone)
                .build();
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).data(userInfoDto).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 개인 정보 수정
    @Transactional
    public ResponseEntity<ApiResponse> updateInfo(UpdateUserInfoDto infoDto, MultipartFile image, User user) throws GeneralSecurityException, IOException {
        if (infoDto == null && image == null) {
            throw new IllegalArgumentException("변경 내용이 없습니다.");
        }

        String imageUrl = image != null ? s3Service.upload(image, PROFILE) : user.getImage();

        String username = user.getUsername();
        String encryptPhone = user.getPhone();
        String phoneHash = user.getPhoneHash();

        if (infoDto != null) {
            if (infoDto.getUsername() != null) {
                if (!infoDto.getUsername().equals(username) && checkUsername(infoDto.getUsername())) {
                    throw new IllegalArgumentException("이미 존재하는 username 입니다.");
                }
                username = infoDto.getUsername();
            }
            if (infoDto.getPhone() != null) {
                String phone = infoDto.getPhone();
                phoneHash = sha256Hashing(phone);
                if (!infoDto.getPhone().equals(encryptUtil.decrypt(encryptPhone)) && checkPhone(phoneHash)) {
                    throw new IllegalArgumentException("이미 존재하는 휴대폰 번호입니다.");
                }
                encryptPhone = encryptUtil.encrypt(phone);
            }
        }

        User updateUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(username)
                .phone(encryptPhone)
                .phoneHash(phoneHash)
                .password(user.getPassword())
                .provider(user.getProvider())
                .point(user.getPoint())
                .image(s3Service.generateOriginalImageUrl(imageUrl, PROFILE))
                .resizedImage(imageUrl)
                .build();

        userRepository.save(updateUser);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg("개인정보 수정 완료").build();
        return ResponseEntity.ok(apiResponse);
    }

    // 비밀번호 변경
    @Transactional
    public ResponseEntity<ApiResponse> resetPassword(UpdatePasswordDto requestDto, User user, String code) {
        if (!checkCode(user, code)) {
            throw new IllegalArgumentException("코드가 일치하지 않습니다.");
        }
        if (requestDto.getPassword() == null) {
            throw new IllegalArgumentException("새비밀번호를 입력해주세요.");
        }

        String newPassword = passwordEncoder.encode(requestDto.getPassword());

        user.updatePassword(newPassword);
        userRepository.save(user);

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

    // 포인트 적립/차감 API
    @Transactional
    public ResponseEntity<ApiResponse> updatePoint(User user, String category) {
        Long currentPoint = user.getPoint();
        Long point = getPoint(category);
        log.info("적립/차감 전 point = " +point);
        String msg;

        if (category.equals("upload") || category.equals("like") || category.equals("share")) {
            user.setPoint(currentPoint += point);
            msg = "포인트가 적립되었습니다";
        } else {
            if (currentPoint < point) {
                throw new IllegalArgumentException("잔여 포인트가 부족합니다.");
            }
            user.setPoint(currentPoint -= point);
            msg = "포인트가 차감되었습니다.";
        }
        log.info("적립/차감 후 point = "+ user.getPoint());
        userRepository.save(user);
        ApiResponse apiResponse = ApiResponse.builder().status(HttpStatus.OK).msg(msg).build();
        return ResponseEntity.ok(apiResponse);
    }

    // 카테고리별 포인트 적립/차감액 조회 메서드
    private Long getPoint(String category) {
        Map<String,Long> categoryList = PointCategory.getCategoryList();

        if (!categoryList.containsKey(category)) {
            throw new IllegalArgumentException("category 값이 올바르지 않습니다.");
        }
        return categoryList.get(category);
    }

    // 메일로 전송한 인증코드 일치 확인 메서드
    public Boolean checkCode(User user, String code) {
        String key = user.getEmail() + "(" + user.getProvider() + ")";
        String authCode = redisUtil.getData(key);
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
    private boolean checkPhone(String phoneHash) {
        // phone 데이터를 해싱한 값으로 repository 에서 동일한 해싱 값을 가진 데이터가 있는지 확인
        return userRepository.existsByPhoneHash(phoneHash);
    }

    // SHA-256 Hashing 메서드 (복호화 불가)
    private String sha256Hashing(String phone) throws NoSuchAlgorithmException {
        try {
            // MessageDigest 객체를 SHA-256 알고리즘으로 초기화
            MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            // 평문 데이터를 바이트 배열로 변환
            byte[] phoneBytes = phone.getBytes();
            // 위에서 변환한 바이트 배열을 sha256Digest 객체에 전달하여 Hash 값을 계산 후 바이트 배열로 반환
            byte[] hashBytes = sha256Digest.digest(phoneBytes);

            StringBuilder hexHashString = new StringBuilder();

            // 계산된 Hash 값을 16진수 문자열로 변환하여 StringBuilder에 저장
            for (byte b : hashBytes) {
                String hexHash = String.format("%02x", b); // 하나의 byte 마다 2자리 16진수 문자로 변환
                hexHashString.append(hexHash);
            }

            return hexHashString.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException("SHA-256 알고리즘을 사용할 수 없습니다.");
        }
    }
}
