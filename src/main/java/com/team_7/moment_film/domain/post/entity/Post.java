package com.team_7.moment_film.domain.post.entity;

import com.team_7.moment_film.domain.post.dto.PostRequestDto;
import com.team_7.moment_film.global.config.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Post extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    Long id;

    @Column(nullable = false)
    private String image;


    public Post(PostRequestDto requestDto, String image){
        this.image = image;
    }



}
