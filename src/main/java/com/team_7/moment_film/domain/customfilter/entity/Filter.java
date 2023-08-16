package com.team_7.moment_film.domain.customfilter.entity;

import com.team_7.moment_film.domain.customfilter.dto.FilterRequestDto;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "custom_filter")
@NoArgsConstructor
public class Filter {
    @Id
    @Column(name = "filter_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filterName;
    private String blur;
    private String brightness;
    private String contrast;
    private String saturate;
    private String sepia;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Filter(FilterRequestDto requestDto, User user){
        this.filterName = requestDto.getFilterName();
        this.blur = requestDto.getBlur();
        this.brightness = requestDto.getBrightness();
        this.contrast = requestDto.getContrast();
        this.saturate = requestDto.getSaturate();
        this.sepia = requestDto.getSepia();
        this.user = user;
    }

}
