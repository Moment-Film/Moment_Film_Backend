package com.team_7.moment_film.domain.comment.entity;

import com.team_7.moment_film.domain.post.entity.Post;
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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id") // 수정: post_id로 변경
//    private Post post; // 수정: Post 타입으로 변경

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "users_id")
    private User writer;

}