package com.team_7.moment_film.domain.user.entity;

import com.team_7.moment_film.domain.customfilter.entity.FilterBookMark;
import com.team_7.moment_film.domain.customframe.entity.FrameBookMark;
import com.team_7.moment_film.domain.follow.entity.Follow;
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

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column
    private String provider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postList;

    @Column
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followerList;

    @Column
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followingList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FilterBookMark> filterBookMarkList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FrameBookMark> frameBookMarkList;
}
