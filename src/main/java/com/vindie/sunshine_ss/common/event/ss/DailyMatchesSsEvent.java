package com.vindie.sunshine_ss.common.event.ss;

import lombok.Getter;

import java.util.Collection;

@Getter
public class DailyMatchesSsEvent extends SsEvent {
    private final Collection<Long> accIds;
    public DailyMatchesSsEvent(Collection<Long> accIds) {
        super(Type.DAILY_MATCHES);
        this.accIds = accIds;
    }
}
