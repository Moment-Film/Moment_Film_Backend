package com.team_7.moment_film.global.security;

import com.team_7.moment_film.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final User user;

    // User 객체 반환하는 메서드
    public User getUser() {
        return user;
    }

    // User의 Id를 반환하는 메서드
    public Long getId() {
        return user.getId();
    }

    // 사용자의 비밀번호를 반환하는 메서드
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 사용자의 이름을 반환하는 메서드
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 사용자의 권한 목록(list)을 반환하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("USER");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

    // 계정이 만료되지 않았는지 확인하는 메서드
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있지 않은지 확인하는 메서드
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명(비밀번호)이 만료되지 않았는지 확인하는 메서드
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화 상태인지 확인하는 메서드
    @Override
    public boolean isEnabled() {
        return true;
    }
}
