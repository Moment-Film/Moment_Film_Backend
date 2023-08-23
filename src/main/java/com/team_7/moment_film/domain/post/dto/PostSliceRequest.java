package com.team_7.moment_film.domain.post.dto;

import jakarta.annotation.Nullable;


public record PostSliceRequest(
        @Nullable
        Long id,
        Integer size,
        Integer page
) {

    public PostSliceRequest(
            @Nullable
            Long id,
            Integer size,
            Integer page
    ) {
        this.id = id;
        this.size = size == null ? 20 : size;
        this.page = page == null ? 1 : page;
    }

}
