package com.team_7.moment_film.domain.comment.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubCommentRequestDTO {
    @Lob
    private String content;
}