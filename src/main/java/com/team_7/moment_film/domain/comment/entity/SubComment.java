package com.team_7.moment_film.domain.comment.entity;

import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.config.TimeStamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;


@NoArgsConstructor
@Builder
@Getter
@Entity
@AllArgsConstructor
public class SubComment extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "users_id")
    private User writer;

}