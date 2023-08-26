package com.team_7.moment_film.global.dto;

import lombok.Getter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
public class PageCustom<T>{
    private List<T> content;
    private PageableDto pageableDto;

    public PageCustom(List<T> content, Pageable pageable, Long total){
        this.content = content;
        this.pageableDto = new PageableDto(new PageImpl<>(content, pageable, total));
    }
}