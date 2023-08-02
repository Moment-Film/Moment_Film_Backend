package com.team_7.moment_film.domain.post.dto;

import org.springframework.web.multipart.MultipartFile;

public class PostRequestDto {
    private Long id;
    private String image;
    private MultipartFile imageFile;


    public PostRequestDto(Long id, String image, MultipartFile imageFile){
        this.id = id;
        this.image = image;
        this.imageFile = imageFile;
    }


}
