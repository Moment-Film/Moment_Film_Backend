package com.team_7.moment_film.domain.post.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team_7.moment_film.domain.like.entity.QLike;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.post.entity.TempPost;
import com.team_7.moment_film.domain.user.entity.QUser;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.team_7.moment_film.domain.post.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class
PostQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;


    //공통
    private JPAQuery<Post> baseQuery(Long id) {
        return jpaQueryFactory.selectFrom(post)
                .where(ltPostId(id));
    }

    //최신순 (무한스크롤)
    public List<Post> getSliceOfPost(@Nullable Long id, int size, int page) {
        int offset = (page - 1) * size;
        return baseQuery(id)
                .orderBy(post.id.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    //조회수 (무한스크롤)
    public List<Post> findAllOrderByViewCountDesc(@Nullable Long id, int size, int page) {
        int offset = (page - 1) * size;
        return baseQuery(id)
                .orderBy(post.viewCount.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    //좋아요 (무한스크롤)
    public List<Post> findAllOrderByLikeCountDesc(@Nullable Long id, int size, int page) {
        int offset = (page - 1) * size;
        return baseQuery(id)
                .orderBy(post.likeList.size().desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    // 내가 작성한 게시글(필요한 필드만)
    public List<TempPost> getMyPosts(Long id) {
        List<TempPost> postList = jpaQueryFactory
                .select(Projections.constructor(TempPost.class, post.id, post.title, post.contents, post.image))
                .from(post)
                .where(post.user.id.eq(id))
                .orderBy(post.createdAt.desc())
                .fetch();
        return  postList;
    }


    // 내가 좋아요한 게시글(필요한 필드만)
    public List<TempPost> getLikedPosts(Long userId) {
        QUser user = QUser.user;
        QLike like =QLike.like;
        SubQueryExpression<Long> subquery = JPAExpressions
                .select(like.post.id)
                .from(user)
                .leftJoin(like).on(user.id.eq(like.user.id))
                .where(user.id.eq(userId));


        List<TempPost> likedPostList = jpaQueryFactory
                .select(Projections.constructor(TempPost.class, post.id, post.title, post.contents, post.image, post.user))
                .from(post)
                .where(post.id.in(subquery))
                .fetch();

        return likedPostList;
    }





    public boolean isLastPage(List<Post> posts, int size){
        return posts.size() < size;
    }
    private BooleanExpression ltPostId(@Nullable Long id) {
        return id == null ? null : post.id.lt(id);
    }
}
