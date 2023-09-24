package com.vindie.sunshine_ss.common.event.ss;

import lombok.Getter;

@Getter
public class DailyMatchesSsEvent extends SsEvent {
    private final long locationId;
    public DailyMatchesSsEvent(long locationId) {
        super(Type.DAILY_MATCHES);
        this.locationId = locationId;
    }
}
