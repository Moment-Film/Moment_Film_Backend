package com.team_7.moment_film.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team_7.moment_film.domain.user.dto.QSearchResponseDto;
import com.team_7.moment_film.domain.user.dto.SearchResponseDto;
import com.team_7.moment_film.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SearchResponseDto> searchUserByName(String userKeyword){
        List<SearchResponseDto> result = queryFactory
                .select(new QSearchResponseDto(QUser.user.username))
                .from(QUser.user)
                .where(QUser.user.username.like("%" + userKeyword + "%"))
                .fetch();
        return result;
    }

}