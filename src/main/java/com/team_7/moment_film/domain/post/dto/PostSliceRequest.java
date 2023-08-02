package com.team_7.moment_film.domain.post.dto;

import jakarta.annotation.Nullable;

public record PostSliceRequest(
        @Nullable
        Long id,
        Integer size
) {

    public PostSliceRequest(
            @Nullable
            Long id,
            Integer size
    ) {
        this.id = id;
        this.size = size == null ? 20 : size;
    }

}
