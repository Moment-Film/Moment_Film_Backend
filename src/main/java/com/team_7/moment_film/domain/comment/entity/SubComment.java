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
@AllArgsConstructor
@Builder
@Getter
@Entity
public class SubComment extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;


    @Lob // 대용량 데이터를 처리하는 @Lob이 꼭 필요할까?
    @Column
    private String content;

    // 대댓글에서 게시글을 참조하는 이유?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id") // 수정: post_id로 변경
    private Post post; // 수정: Post 타입으로 변경

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "users_id")
    private User writer;

    // 위에서 user_id로 User Entity를 참조하는데 아래 두 필드가 테이블에 저장될 필요가 있을까?
    private String username;


    public SubComment(Long id, Comment comment, String content, Post post, User writer,String username,Long userId){
        this.id = id;
        this.comment = comment;
        this.content = content;
        this.post = post;
        this.writer = writer;
        this.username = username;
    }
}