package com.team_7.moment_film.global.config;

import com.team_7.moment_film.global.filter.JwtAuthenticationFilter;
import com.team_7.moment_film.global.filter.JwtAuthorizationFilter;
import com.team_7.moment_film.global.filter.JwtLogoutFilter;
import com.team_7.moment_film.global.handler.CustomAccessDeniedHandler;
import com.team_7.moment_film.global.handler.CustomAuthenticationEntryPoint;
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

        // 요청 url 및 resources에 대한 인가 설정
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/api/user/signup").permitAll()
                        .requestMatchers("/api/user/kakao/*").permitAll()
                        .requestMatchers("/api/user/popular").permitAll()
                        .requestMatchers("/api/user/search").permitAll()
                        .requestMatchers(GET, "/api/user/profile/*").permitAll()
                        .requestMatchers(GET, "/api/post").permitAll()
                        .requestMatchers(GET, "/api/post/*").permitAll()
                        .requestMatchers("/upload").permitAll()
                        .requestMatchers(GET, "/api/filter").permitAll()
                        .requestMatchers(GET, "/api/frame").permitAll()
                        .anyRequest().authenticated()
        );

        // 예외처리
        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
        );

        // 필터 순서 (인가 -> 인증)
        http.addFilterBefore(jwtLogoutFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
