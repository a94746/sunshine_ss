package com.vindie.sunshine_ss.common.record.event.ss;

import com.vindie.sunshine_ss.common.record.NotifRecord;
import lombok.Getter;

@Getter
public class QueueNotifsSsEvent extends SsEvent {
    private final NotifRecord notifRecord;
    public QueueNotifsSsEvent(NotifRecord notifRecord) {
        super(Type.QUEUE_NOTIFS);
        this.notifRecord = notifRecord;
    }
}