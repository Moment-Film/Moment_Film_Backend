package com.team_7.moment_film.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    // redis 데이터 저장 메서드 (key : username, val: refreshToken, expiredTime : TTL)
    public void setData(String key, String val, Date expiredTime) {
        long expirationInSeconds = (expiredTime.getTime() - System.currentTimeMillis()) / 1000;
        Duration duration = Duration.ofSeconds(expirationInSeconds);
        redisTemplate.opsForValue().set(key, val, duration);
    }

    // redis 데이터 조회 메서드
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key).toString();
    }

    // redis 데이터 삭제 메서드
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
