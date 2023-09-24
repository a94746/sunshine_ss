package com.vindie.sunshine_ss.common.event.ui;

import com.vindie.sunshine_ss.queue.dto.QueueElement;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class Stub implements RealTimeSederUI {
    @Override
    public List<Long> send(Map<Long, List<QueueElement>> queueElements) {
        return Collections.emptyList();
    }
}
