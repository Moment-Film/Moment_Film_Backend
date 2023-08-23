package com.team_7.moment_film.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ViewCountUtil {

    private static final Map<Long, Map<String, Long>> postViewCountMap = new ConcurrentHashMap<>();
//    private static final int VIEW_COUNT_INTERVAL = 1000*60; // 1분 개발용
    private static final int VIEW_COUNT_INTERVAL = 1000*60*60*24; // 24시간 기준
    public static synchronized boolean canIncreaseViewCount(Long postId, String ip) {
        Map<String, Long> ipLastAccessMap = postViewCountMap.computeIfAbsent(postId, k -> new ConcurrentHashMap<>());
        Long lastAccessTime = ipLastAccessMap.get(ip);
        log.info("Ip 확인 : " + lastAccessTime);
        if (lastAccessTime == null || System.currentTimeMillis() - lastAccessTime > VIEW_COUNT_INTERVAL) {
            ipLastAccessMap.put(ip, System.currentTimeMillis());
            log.info("Ip 확인2 : " + ipLastAccessMap.put(ip, System.currentTimeMillis()));
            return true;
        }
        return false;
    }

}
