package com.team_7.moment_film.domain.user.entity;

import com.team_7.moment_film.domain.follow.entity.Follow;
import com.team_7.moment_film.domain.like.entity.Like;
import com.team_7.moment_film.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 동일한 패키지 및 자식 클래스에서만 접근 가능
@AllArgsConstructor(access = AccessLevel.PROTECTED) // 동일한 패키지 및 자식 클래스에서만 접근 가능
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column
    private boolean isKakao;

    @OneToMany(mappedBy = "user")
    private List<Post> postList;

    @Column
    @OneToMany(mappedBy = "follower")
    private List<Follow> followerList;

    @Column
    @OneToMany(mappedBy = "following")
    private List<Follow> followingList;
}
