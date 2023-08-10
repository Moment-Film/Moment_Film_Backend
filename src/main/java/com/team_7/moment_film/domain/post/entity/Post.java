package com.team_7.moment_film.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.team_7.moment_film.domain.comment.entity.Comment;
import com.team_7.moment_film.domain.comment.entity.SubComment;
import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.global.config.TimeStamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.yaml.snakeyaml.comments.CommentLine;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.annotations.FetchMode.SUBSELECT;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Post extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    Long id;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Transient
    private String username;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Where(clause = "parent_id is null")
//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") // 추가
//    private List<Comment> children = new ArrayList<>();

    @ColumnDefault("0")
    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @ColumnDefault("0")
    @Column(name = "view_count",nullable = false)
    private Integer viewCount;

    @ColumnDefault("0")
    @Column(name = "comment_count",nullable = false)
    private Integer commentCount;


    @Fetch(SUBSELECT)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubComment> subComments = new ArrayList<>();



    // 이 부분부터 증가 함수들 추가
    public void increaseLikeCount() {
        this.likeCount++;
    }

    public int getCommentCount() {
        int totalCommentCount = commentList.size();

        for (Comment comment : commentList) {
            totalCommentCount += comment.getSubComments().size();
        }

        return totalCommentCount;
    }


    public List<Comment> getAllCommentsWithSubComments() {
        List<Comment> allCommentsWithSubComments = new ArrayList<>();

        for (Comment comment : commentList) {
            Comment newComment = Comment.builder()
                    .id(comment.getId())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .content(comment.getContent())
                    .build();

            List<SubComment> newSubComments = new ArrayList<>();
            for (SubComment subComment : comment.getSubComments()) {
                SubComment newSubComment = SubComment.builder()
                        .id(subComment.getId())
                        .userId(user.getId())
                        .username(subComment.getWriter().getUsername())
                        .content(subComment.getContent())
                        .build();
                newSubComments.add(newSubComment);
            }

            newComment.setSubComments(newSubComments);
            allCommentsWithSubComments.add(newComment);
        }

        return allCommentsWithSubComments;
    }


    public List<Integer> getCommentSubCommentCounts() {
        List<Integer> commentSubCommentCounts = new ArrayList<>();

        for (Comment comment : commentList) {
            int subCommentCount = comment.getSubComments().size();
            commentSubCommentCounts.add(subCommentCount);
        }

        return commentSubCommentCounts;
    }


}
