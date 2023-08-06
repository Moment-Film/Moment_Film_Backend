package com.team_7.moment_film.domain.post.dto;

import com.team_7.moment_film.domain.post.entity.Post;


public record PostSliceResponse(
        long id,
        String image
) {

    public static PostSliceResponse from(Post post) {
        return new PostSliceResponse(post.getId(), post.getImage());
    }

}
