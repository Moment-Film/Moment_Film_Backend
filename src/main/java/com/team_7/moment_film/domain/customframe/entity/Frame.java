package com.team_7.moment_film.domain.customframe.entity;

import com.team_7.moment_film.domain.customframe.dto.FrameRequestDto;
import com.team_7.moment_film.domain.post.Post;
import com.team_7.moment_film.domain.user.User;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Frame(FrameRequestDto requestDto, User user){
        this.frameName = requestDto.getFrameName();
        this.user = user;
    }

}
