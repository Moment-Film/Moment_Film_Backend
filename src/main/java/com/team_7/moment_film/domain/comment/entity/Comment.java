package com.team_7.moment_film.domain.comment.entity;

import com.fasterxml.jackson.annotation.*;
import com.team_7.moment_film.domain.post.entity.Post;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.config.TimeStamped;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "comments")
@EqualsAndHashCode(exclude = "children")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Comment extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Lob
    private String content;


    @ColumnDefault("FALSE")
    @Column(nullable = false)
    private Boolean isDeleted;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "users_id")
    private User writer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore   // 양방향 연관 관계의 한쪽 필드를 무시함
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @JsonManagedReference // 자식 엔티티들은 부모 엔티티를 직렬화하지 않도록 설정
    private List<Comment> children = new ArrayList<>();



    public Comment(String content) {
        this.content = content;
    }

    public void updateWriter(User user) {
        this.writer = user;
    }

    public void updatePost(Post post) {
        this.post = post;
    }

    public void updateParent(Comment comment) {
        this.parent = comment;
    }

    public void changeIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void updateContent(String content) {
        this.content = content;
    }

}
