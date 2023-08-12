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

    // Post Entity에서 subComments가 Post Entity 대상으로 orphanRemoval 옵션이 걸려져 있는데 여기서 한 번 더 거는 이유?
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubComment> subComments = new ArrayList<>();

    // Builder pattern과 생성자 pattern을 혼용해서 사용하는 이유?
    @Builder
    public Comment(Long id, String content, boolean isDeleted, Post post, User writer, String username, Long userId){
        this.id = id;
        this.content = content;
        this.post = post;
        this.writer = writer;
        this.username = username;
        this.userId = userId;
    }

    // @Getter가 달려있는데 Get 메서드가 필요한 이유?
    public List<SubComment> getSubComments() {
        return subComments;
    }
    // Set메서드가 필요한 이유?
    public void setSubComments(List<SubComment> subComments) {
        this.subComments = subComments;
    }

}
