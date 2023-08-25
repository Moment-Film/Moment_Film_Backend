package com.team_7.moment_film.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoDto {
    private String username;
    private String image;
    private String email;
    private String phone;
}