package com.vindie.sunshine_ss.scheduling.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleInfo {
    private Long locationId;
    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime lastUpdate = LocalDateTime.now();

    public ScheduleInfo(Long locationId, LocalDateTime startTime) {
        this.locationId = locationId;
        this.lastUpdate = startTime;
        this.startTime = startTime;
    }
}
