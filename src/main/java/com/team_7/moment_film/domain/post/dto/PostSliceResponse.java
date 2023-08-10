package com.team_7.moment_film.domain.post.dto;

import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;


public record PostSliceResponse(
        long id,
        String title,
        String image,
        String username,
        Integer likeCount,
        Integer viewCount,
        Integer commentCount

) {

    public static PostSliceResponse from(Post post) {
        return new PostSliceResponse(post.getId(), post.getTitle(),post.getImage(), post.getUser().getUsername(),
                post.getLikeCount(), post.getViewCount(), post.getCommentCount());
    }

}
