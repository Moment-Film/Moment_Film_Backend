package com.team_7.moment_film.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team_7.moment_film.domain.user.dto.PopularUserResponseDto;
import com.team_7.moment_film.domain.user.dto.SearchResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team_7.moment_film.domain.follow.entity.QFollow.follow;
import static com.team_7.moment_film.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SearchResponseDto> searchUserByName(String userKeyword){
        List<SearchResponseDto> result = queryFactory
                .select(Projections.constructor(SearchResponseDto.class,user.username))
                .from(user)
                .where(user.username.like("%" + userKeyword + "%"))
                .fetch();
        return result;
    }

    @Override
    public List<PopularUserResponseDto> getPopularUser(){
        List<PopularUserResponseDto> result = queryFactory
                .select(Projections.constructor(PopularUserResponseDto.class, user.id, user.username, follow.follower.count().as("follower")))
                .from(user)
                .leftJoin(follow).on(user.id.eq(follow.following.id))
                .groupBy(user.id)
                .orderBy(follow.follower.count().desc())
                .limit(10)
                .fetch();
        return result;
    }

}