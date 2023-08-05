package com.team_7.moment_film.domain.user.repository;

import com.team_7.moment_film.domain.user.dto.PopularUserResponseDto;
import com.team_7.moment_film.domain.user.dto.SearchResponseDto;

import java.util.List;

public interface UserRepositoryCustom {
        List<SearchResponseDto> searchUserByName(String userKeyword);

        List<PopularUserResponseDto> getPopularUser();
}
