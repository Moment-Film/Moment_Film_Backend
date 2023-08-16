package com.team_7.moment_film.global.security;

import com.team_7.moment_film.domain.user.entity.User;
import com.team_7.moment_film.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        if (userId.contains("@")) {
            String email = userId;
            String provider = "momentFilm";
            User user = userRepository.findByEmailAndProvider(email, provider)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
            return new UserDetailsImpl(user);
        }
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
        return new UserDetailsImpl(user);
    }
}
