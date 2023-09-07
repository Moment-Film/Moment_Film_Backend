package com.team_7.moment_film.domain.user.repository;

import com.team_7.moment_film.domain.user.dto.PopularUserResponseDto;
import com.team_7.moment_film.domain.user.dto.SearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {
        Page<SearchResponseDto> searchUserByName(String userKeyword, Pageable pageable);
        List<PopularUserResponseDto> getPopularUser();
}
