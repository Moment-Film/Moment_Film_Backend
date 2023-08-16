package com.team_7.moment_film.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JWT Util")
@Component
public class JwtUtil {
    public static final String HEADER_ACCESS_TOKEN = "accessToken";
    public static final String HEADER_REFRESH_TOKEN = "refreshToken";
    public static final String BEARER_PREFIX = "Bearer ";
    //    private final Long ACCESS_TOKEN_EXPIRATION = 6 * 60 * 60 * 1000L; // 6시간
//    private final Long REFRESH_TOKEN_EXPIRATION = 12 * 60 * 60 * 1000L; // 12시간
    private final Long ACCESS_TOKEN_EXPIRATION = 30 * 60 * 1000L; // 30분
    private final Long REFRESH_TOKEN_EXPIRATION = 1 * 60 * 60 * 1000L; // 1시간

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    // JWT 생성 시 secretKey를 decode 후 Key 객체에 대입
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT(accessToken) 생성 메서드
    public String createAccessToken(Long id, String username, String email, String provider) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(id)) // 토큰 식별자 : UserId
                        .claim("username", username)
                        .claim("email", email)
                        .claim("provider", provider)
                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRATION))
                        .setIssuedAt(date)
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
    }

    // JWT(refreshToken) 생성 메서드
    public String createRefreshToken(Long id, String username, String email, String provider) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(id)) // 토큰 식별자 : UserId
                        .claim("username", username)
                        .claim("email", email)
                        .claim("provider", provider)
                        .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_EXPIRATION))
                        .setIssuedAt(date)
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
    }

    // JWT Bearer SubString 메서드
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith("Bearer")) {
            return tokenValue.substring(7);
        }
        log.error("유효하지 않은 토큰");
        throw new NullPointerException("유효하지 않은 토큰");
    }

    // JWT 검증 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            log.warn("JWT token validation failed, JWT 토큰 유효성 검증에 실패했습니다.");
//            throw new SecurityException("JWT token validation failed, JWT 토큰 유효성 검증에 실패했습니다.");
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token, 토큰의 유효 기간이 만료되었습니다.");
//            throw new ExpiredJwtException(null, null,"Expired JWT token, 토큰의 유효 기간이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token, 지원되지 않는 JWT 토큰입니다.");
//            throw new UnsupportedJwtException("Unsupported JWT token, 지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims is empty, 잘못된 JWT 토큰입니다.");
//            throw new IllegalArgumentException("JWT claims is empty, 잘못된 JWT 토큰입니다.");
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token, 유효하지 않은 JWT 토큰 형식입니다.");
//            throw new MalformedJwtException("Invalid JWT token, 유효하지 않은 JWT 토큰 형식입니다.");
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature, 유효하지 않은 JWT 서명입니다.");
//            throw new SignatureException("Invalid JWT signature, 유효하지 않은 JWT 서명입니다.");
        }
        return false;
    }

    // JWT의 사용자 정보 가져오는 메서드
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // refreshToken 가져오는 메서드
    public String getRefreshToken(String refreshToken) {
        if (StringUtils.hasText(refreshToken)) {
            return substringToken(refreshToken);
        }
        return null;
    }
}
