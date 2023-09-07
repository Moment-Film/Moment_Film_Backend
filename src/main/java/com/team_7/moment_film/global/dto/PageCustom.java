package com.team_7.moment_film.global.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageCustom<T>{
    private List<T> content;
    private boolean first; // 시작 페이지 여부
    private int pageSize; // 페이지 당 데이터 수
    private int pageNumber; // 페이지 번호
    private int numberOfElements; // 현재 페이지 데이터 수
    private long totalElements; // 총 데이터 수

    public PageCustom(Page<T> page){
        this.content = page.getContent();
        this.first = page.isFirst();
        this.pageSize = page.getSize();
        this.pageNumber = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();
        this.totalElements = page.getTotalElements();
    }
}