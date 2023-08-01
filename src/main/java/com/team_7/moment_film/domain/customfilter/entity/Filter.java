package com.team_7.moment_film.domain.customfilter.entity;

import com.team_7.moment_film.domain.customfilter.dto.FilterRequestDto;
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

    private String filterName;

    private int filterValue;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @OneToOne
//    @JoinColumn(name = "post_id")
//    private Post post;

    public Filter(FilterRequestDto requestDto){
        this.filterName = requestDto.getFilterName();
        this.filterValue = requestDto.getFilterValue();
    }

}
