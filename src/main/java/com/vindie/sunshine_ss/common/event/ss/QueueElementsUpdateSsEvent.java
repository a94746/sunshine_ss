package com.vindie.sunshine_ss.common.event.ss;

import lombok.Getter;

import java.util.List;

@Getter
public class QueueElementsUpdateSsEvent extends SsEvent {
    private final List<Long> ids;
    public QueueElementsUpdateSsEvent(List<Long> ids) {
        super(Type.QUEUE_ELEMENTS_UPDATE);
        this.ids = ids;
    }
}