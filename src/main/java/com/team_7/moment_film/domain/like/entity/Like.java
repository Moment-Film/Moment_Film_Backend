package com.team_7.moment_film.domain.like.entity;


import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id")
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;
    @Builder
    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
