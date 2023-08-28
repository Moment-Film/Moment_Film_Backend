package com.team_7.moment_film.global.config;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimeStamped {
    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @LastModifiedDate
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    @PrePersist
    public void onPrePersist() {
        this.createdAt = new Date();
        this.modifiedAt = this.createdAt;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modifiedAt = new Date();
    }

    public String getCreatedAtFormatted() {
        return formatLocalDateTime(this.createdAt);
    }

    public String getModifiedAtFormatted() {
        return formatLocalDateTime(this.modifiedAt);
    }

    private String formatLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
