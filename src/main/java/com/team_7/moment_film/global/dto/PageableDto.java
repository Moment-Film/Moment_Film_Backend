package com.team_7.moment_film.global.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;
@Getter
public class PageableDto {
    private boolean first; // 시작 페이지 여부
    private int pageSize; // 페이지 당 데이터 수
    private int pageNumber; // 페이지 번호
    private int numberOfElements; // 현재 페이지 데이터 수

    public PageableDto(Page page){
        this.first = page.isFirst();
        this.pageSize = page.getSize();
        this.pageNumber = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();
    }

}