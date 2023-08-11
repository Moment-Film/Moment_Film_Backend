package com.team_7.moment_film.domain.follow.service;

import com.team_7.moment_film.domain.follow.entity.Follow;
import com.team_7.moment_film.domain.follow.repository.FollowRepository;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import com.team_7.moment_film.global.dto.CustomResponseEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public CustomResponseEntity<String> followUser(User user, Long userId) {
        User toUser = userRepository.findById(userId).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        if(!user.getId().equals(userId)){
            if(followed(user.getId(), userId)){
                throw new IllegalArgumentException("이미 팔로우한 사용자입니다.");
            }
            followRepository.save(Follow.builder().follower(user).following(toUser).build());
        }else{
            throw new IllegalArgumentException("본인 외 사용자를 선택해주세요.");
        }
        return CustomResponseEntity.msgResponse(HttpStatus.OK,"팔로우 완료");
    }

    @Transactional
    public CustomResponseEntity<String> unfollowUser(User user, Long userId) {
        userRepository.findById(userId).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 사용자입니다."));
        if(!followed(user.getId(),userId)){
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        followRepository.deleteByFollowerIdAndFollowingId(user.getId(),userId);
        return CustomResponseEntity.msgResponse(HttpStatus.OK,"팔로우 취소 완료");
    }

     //팔로우 확인 메서드
    private boolean followed(Long fromUserId, Long toUserId) {
        return followRepository.existsByFollowerIdAndFollowingId(fromUserId, toUserId);
    }

}