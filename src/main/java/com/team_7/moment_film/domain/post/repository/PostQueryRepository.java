package com.team_7.moment_film.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team_7.moment_film.domain.post.dto.PostSliceRequest;
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

    public List<Post> getSliceOfPost(
            @Nullable
            Long id,
            int size
    ) {
        return jpaQueryFactory.selectFrom(post)
                .where(ltPostId(id))
                .join(post)
                .fetchJoin()
                .where(post.id.eq(post.id))
                .orderBy(post.id.desc())
                .limit(size)
                .fetch();

    }

//    public List<Post> findAllOrderByViewCountDesc(
//            @Nullable
//            Long id,
//            int size) {
//        return jpaQueryFactory
//                .selectFrom(post)
//                .where(ltPostId(id))
//                .orderBy(post.viewCount.desc())
//                .limit(size)
//                .fetch();
//    }
//
//
//    public List<Post> findAllOrderByLikeCountDesc(
//            @Nullable
//            Long id,
//            int size){
//        return jpaQueryFactory
//                .selectFrom(post)
//                .where(ltPostId(id))
//                .orderBy(post.likeCount.desc())
//                .limit(size)
//                .fetch();
//    }





    private BooleanExpression ltPostId(@Nullable Long id) {
        return id == null ? null : post.id.lt(id);
    }
}
