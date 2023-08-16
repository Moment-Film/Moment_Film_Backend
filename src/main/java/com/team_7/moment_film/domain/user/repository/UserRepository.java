package com.team_7.moment_film.domain.user.repository;

import com.team_7.moment_film.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> ,UserRepositoryCustom{
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndProvider(String email, String provider);
}
