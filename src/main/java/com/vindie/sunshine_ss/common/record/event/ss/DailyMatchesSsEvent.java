package com.vindie.sunshine_ss.common.record.event.ss;

import lombok.Getter;

import java.util.Collection;

@Getter
public class DailyMatchesSsEvent extends SsEvent {
    private final Collection<Long> accIds;
    public DailyMatchesSsEvent(Collection<Long> accountIds) {
        super(Type.DAILY_MATCHES);
        this.accIds = accountIds;
    }
}
