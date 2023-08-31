package com.team_7.moment_film.domain.post.dto;

import com.team_7.moment_film.domain.post.entity.Post;


public record PostSliceResponse(
        long id,
        String title,
        String image,
        String username,
        String userImage,
        Long viewCount,
        int likeCount,
        int commentCount


) {

    public static PostSliceResponse from(Post post) {
        return new PostSliceResponse(post.getId(), post.getTitle(), post.getImage(), post.getUser().getUsername(),post.getUser().getResizedImage() ,post.getViewCount(), post.getLikeList().size(),post.getCommentList().size());
    }

}
