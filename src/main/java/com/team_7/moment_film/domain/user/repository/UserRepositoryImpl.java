package com.team_7.moment_film.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team_7.moment_film.domain.post.dto.PostSearchDto;
import com.team_7.moment_film.domain.user.dto.PopularUserResponseDto;
import com.team_7.moment_film.domain.user.dto.SearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.team_7.moment_film.domain.follow.entity.QFollow.follow;
import static com.team_7.moment_film.domain.post.entity.QPost.post;
import static com.team_7.moment_film.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    // 사용자 검색
    @Override
    public Page<SearchResponseDto> searchUserByName(String userKeyword, Pageable pageable) {
        List<SearchResponseDto> result = queryFactory
                .select(Projections.constructor(SearchResponseDto.class,
                        user.id,
                        user.username,
                        user.image,
                        post.count(),
                        JPAExpressions
                                .select(follow.follower.count())
                                .from(follow)
                                .where(follow.following.id.eq(user.id)),
                        JPAExpressions
                                .select(follow.following.count())
                                .from(follow)
                                .where(follow.follower.id.eq(user.id))
                ))
                .from(user)
                .leftJoin(post).on(user.id.eq(post.user.id))
                .groupBy(user.id)
                .where(usernameEq(userKeyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 최근 작성한 게시물 리스트
        for (SearchResponseDto dto : result) {
            List<PostSearchDto> postList = queryFactory
                    .select(Projections.constructor(PostSearchDto.class, post.id, post.image))
                    .from(post)
                    .where(post.user.id.eq(dto.getId()))
                    .orderBy(post.createdAt.desc())
                    .limit(3)
                    .fetch();

            dto.setPostList(postList);
        }

        // 총 데이터 수 구하기
        JPAQuery<Long> total= queryFactory
                .select(user.count())
                .from(user)
                .where(usernameEq(userKeyword));

        return PageableExecutionUtils.getPage(result, pageable, total::fetchOne);
    }

    // 대소문자 상관없이 keyword가 username에 포함되는지 확인
    private BooleanExpression usernameEq(String userKeyword){
        return user.username.containsIgnoreCase(userKeyword);
    }

    // 팔로워 순으로 사용자 조회
    @Override
    public List<PopularUserResponseDto> getPopularUser(){
        List<PopularUserResponseDto> result = queryFactory
                .select(Projections.constructor(PopularUserResponseDto.class,
                        user.id,
                        user.username,
                        user.image,
                        follow.follower.count(),
                        JPAExpressions
                                .select(post.count())
                                .from(post)
                                .where(post.user.id.eq(user.id))
                ))
                .from(user)
                .leftJoin(follow).on(user.id.eq(follow.following.id))
                .groupBy(user.id)
                .orderBy(follow.follower.count().desc())
                .limit(10)
                .fetch();
        return result;
    }

}