package com.team_7.moment_film.domain.customfilter.entity;

import com.team_7.moment_film.domain.customfilter.dto.FilterRequestDto;
import com.team_7.moment_film.domain.post.Post;
import com.team_7.moment_film.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "custom_filter")
@NoArgsConstructor
public class Filter {
    @Id
    @Column(name = "filter_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String blur;
    private String contrast;
    private String grayscale;
    private String sepia;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Filter(FilterRequestDto requestDto, User user){
        this.blur = requestDto.getBlur();
        this.contrast = requestDto.getContrast();
        this.grayscale = requestDto.getGrayscale();
        this.sepia = requestDto.getSepia();
        this.user = user;
    }

}
