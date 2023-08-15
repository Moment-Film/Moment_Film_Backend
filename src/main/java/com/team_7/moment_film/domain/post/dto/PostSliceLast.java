package com.team_7.moment_film.domain.post.dto;

import lombok.NoArgsConstructor;

import java.util.List;



public record PostSliceLast(
        List<PostSliceResponse> responses,
        boolean isLastPage
) {

}