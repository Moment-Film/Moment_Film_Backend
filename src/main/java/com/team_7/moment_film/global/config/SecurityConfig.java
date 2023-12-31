package com.team_7.moment_film.global.config;

import com.team_7.moment_film.global.filter.JwtAuthenticationFilter;
import com.team_7.moment_film.global.filter.JwtAuthorizationFilter;
import com.team_7.moment_film.global.filter.JwtLogoutFilter;
import com.team_7.moment_film.global.exception.CustomAccessDeniedHandler;
import com.team_7.moment_film.global.exception.CustomAuthenticationEntryPoint;
import com.team_7.moment_film.global.security.UserDetailsServiceImpl;
import com.team_7.moment_film.global.util.JwtUtil;
import com.team_7.moment_film.global.util.RedisUtil;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity // spring security 지원을 가능하게 함.
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 를 생성하는 메서드
    // AuthenticationConfiguration 를 통해서만 AuthenticationManager 가 생성됨
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // JwtAuthenticationFilter(인증필터) 에서 AuthenticationManager 를 가져올 수 있도록
    // 위에서 만든 AuthenticationManager 생성 메서드를 호출 후 반환된 Authentication 을 filter 에 세팅
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, redisUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    // JwtAuthorizationFilter에 필요한 JwtUtil, userDetailsService 객체 주입하여 필터 생성
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(jwtUtil, redisUtil, userDetailsService);
    }

    @Bean
    public JwtLogoutFilter jwtLogoutFilter() throws ServletException, IOException {
        return new JwtLogoutFilter(jwtUtil, redisUtil);
    }

    // 위에서 만든 Filter 의 순서와 인증 및 인가를 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 : 쿠키를 사용하지 않기 때문에 비활성화
        http.csrf((csrf) -> csrf.disable());

        // Session 방식으로 사용하지 않고 JWT 방식을 사용하기 위해 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 요청 url 및 resources에 대한 인증/인가 설정
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/api/user/signup").permitAll() // check
                                .requestMatchers("/api/user/kakao/callback").permitAll() // check
                                .requestMatchers("/api/user/google/callback").permitAll()
                                .requestMatchers("/api/user/popular").permitAll() // check
                                .requestMatchers("/api/user/search").permitAll() // check
                                .requestMatchers(GET, "/api/user/profile/*").permitAll() // check
                                .requestMatchers(GET, "/api/post").permitAll() // check
                                .requestMatchers(GET, "/api/post/like").permitAll() // check
                                .requestMatchers(GET, "/api/post/view").permitAll() // check
                                .requestMatchers(GET, "/api/post/{postId}").permitAll() // check
                                .requestMatchers("/swagger-ui/*").permitAll()
                                .requestMatchers("/v3/api-docs").permitAll()
                                .requestMatchers("/v3/api-docs/*").permitAll()
                                .anyRequest().authenticated()
        );

        // 인증/인가 예외처리
        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler) // 접근 권한
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 미인증
        );

        // 필터 순서 (로그아웃 -> 인가 -> 인증)
        http.addFilterBefore(jwtLogoutFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
