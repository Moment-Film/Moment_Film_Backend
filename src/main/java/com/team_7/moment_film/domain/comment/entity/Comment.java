package com.team_7.moment_film.domain.comment.entity;

import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.config.TimeStamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Lob
    private String content;

    private String username;

    private Long userId;

//    @ColumnDefault("FALSE")
//    @Column(nullable = false)
//    private Boolean isDeleted;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "users_id")
    private User writer;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubComment> subComments = new ArrayList<>();
    @Builder
    public Comment(Long id, String content, boolean isDeleted, Post post, User writer, String username, Long userId){
        this.id = id;
        this.content = content;
        this.post = post;
        this.writer = writer;
        this.username = username;
        this.userId = userId;
    }

    public List<SubComment> getSubComments() {
        return subComments;
    }
    public void setSubComments(List<SubComment> subComments) {
        this.subComments = subComments;
    }

}
