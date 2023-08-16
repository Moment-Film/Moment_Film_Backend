package com.team_7.moment_film.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GoogleResponseDto {
    private String access_token;
    private String expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
