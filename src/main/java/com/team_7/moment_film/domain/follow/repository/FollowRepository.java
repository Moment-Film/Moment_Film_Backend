package com.team_7.moment_film.domain.follow.repository;

import com.team_7.moment_film.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    List<Follow> findAllByFollowerId(Long UserId); // 팔로잉 리스트
    List<Follow> findAllByFollowingId(Long UserId); // 팔로워 리스트
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

}
