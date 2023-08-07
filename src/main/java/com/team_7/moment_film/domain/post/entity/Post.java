package com.team_7.moment_film.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.post.dto.PostRequestDto;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.config.TimeStamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;


import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Post extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    Long id;

    @Column(nullable = false)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "parent_id is null")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") // 추가
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Post(PostRequestDto requestDto, String image, User user){
        this.image = image;
        this.user = user;
    }

    public void addComment(Comment comment) {
        this.children.add(comment);
    }



}
