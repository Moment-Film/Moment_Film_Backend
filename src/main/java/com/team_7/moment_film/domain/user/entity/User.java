package com.team_7.moment_film.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String phoneHash;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private Long point;

    @Column
    private String image;

    public void setPoint(Long point) {
        this.point = point;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
