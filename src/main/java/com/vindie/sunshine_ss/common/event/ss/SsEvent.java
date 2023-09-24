package com.vindie.sunshine_ss.common.event.ss;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class SsEvent {
    protected Type type;

    public enum Type {
        QUEUE_ELEMENTS_UPDATE,
        DAILY_MATCHES,
        SINGLE_LIKED_MATCH,
        COUPLE_LIKED_MATCH

    }
}
