package com.team_7.moment_film.domain.comment.entity;

import com.team_7.moment_film.domain.comment.dto.SubCommentRequestDTO;
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
public class SubComment extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commentId", nullable = false)
    private Comment comment;

    @Lob
    @Column
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "users_id")
    private User writer;

    public SubComment(Long id, Comment comment, String content, Post post, User writer){
        this.id = id;
        this.comment = comment;
        this.content = content;
        this.post = post;
        this.writer = writer;
    }

}