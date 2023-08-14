package com.team_7.moment_film.domain.comment.dto;

import com.team_7.moment_film.domain.user.entity.User;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class SubCommentRequestDTO {
    @Lob
    private String content;
}