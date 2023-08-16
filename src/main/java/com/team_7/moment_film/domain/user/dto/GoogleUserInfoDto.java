package com.team_7.moment_film.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfoDto {
    private String id;
    private String email;
    private String verified_email;
    private String name;
    private String given_name;
    private String picture;
    private String locale;
}
