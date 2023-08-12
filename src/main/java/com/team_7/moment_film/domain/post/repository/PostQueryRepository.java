package com.team_7.moment_film.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team_7.moment_film.domain.post.entity.Post;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.team_7.moment_film.domain.post.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;


    //공통
    private JPAQuery<Post> baseQuery(Long id) {
        return jpaQueryFactory.selectFrom(post)
                .where(ltPostId(id))
                .orderBy(post.id.desc());
    }

    //최신순 (무한스크롤)
    public List<Post> getSliceOfPost(@Nullable Long id, int size) {
        return baseQuery(id)
                .limit(size)
                .fetch();
    }

    //조회수 (무한스크롤)
    public List<Post> findAllOrderByViewCountDesc(@Nullable Long id, int size) {
        return baseQuery(id)
                .orderBy(post.viewCount.desc())
                .limit(size)
                .fetch();
    }

    //좋아요 (무한스크롤)
    public List<Post> findAllOrderByLikeCountDesc(@Nullable Long id, int size) {
        return baseQuery(id)
                .orderBy(post.likeList.size().desc())
                .limit(size)
                .fetch();
    }





    private BooleanExpression ltPostId(@Nullable Long id) {
        return id == null ? null : post.id.lt(id);
    }
}
