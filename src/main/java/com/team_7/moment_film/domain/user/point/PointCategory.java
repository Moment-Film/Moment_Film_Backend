package com.team_7.moment_film.domain.user.point;

import java.util.HashMap;
import java.util.Map;

public enum PointCategory {
    UPLOAD("upload", 300L),
    LIKE("like", 10L),
    SHARE("share", 50L),
    SAVE("save", 200L),
    FILTER_AND_FRAME("filterAndFrame", 200L),
    FILTER("filter", 100L),
    FRAME("frame", 100L);

    private final String category;
    private final Long point;

    PointCategory(String category, Long point) {
        this.category = category;
        this.point = point;
    }

    // 카테고리명 Get 메서드
    public String getCategory() {
        return category;
    }

    // 포인트 Get 메서드
    public Long getPoint() {
        return point;
    }

    // 카테고리별 포인트 리스트 Get 메서드
    public static Map<String, Long> getCategoryList() {
        return CATEGORY_LIST;
    }

    // 카테고리별 포인트 리스트 생성 메서드
    private static final Map<String, Long> CATEGORY_LIST = new HashMap<>();

    static {
        for (PointCategory pointCategory : PointCategory.values()) {
            CATEGORY_LIST.put(pointCategory.getCategory(), pointCategory.getPoint());
        }
    }
}
