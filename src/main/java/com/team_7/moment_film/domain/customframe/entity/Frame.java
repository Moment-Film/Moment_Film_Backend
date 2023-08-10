package com.team_7.moment_film.domain.customframe.entity;

import com.team_7.moment_film.domain.customframe.dto.FrameRequestDto;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "custom_frame")
@NoArgsConstructor
public class Frame {
    @Id
    @Column(name = "frame_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String frameName;

    private String image;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Frame(FrameRequestDto requestDto, String image, User user){
        this.frameName = requestDto.getFrameName();
        this.image = image;
        this.user = user;
    }

}
