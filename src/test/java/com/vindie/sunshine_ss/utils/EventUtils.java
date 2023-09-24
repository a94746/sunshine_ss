package com.vindie.sunshine_ss.utils;

import com.vindie.sunshine_ss.common.event.ss.SsEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventUtils {
    public SsEvent event;

    @EventListener
    public void eventListener(SsEvent event) {
        this.event = event;
    }
}
