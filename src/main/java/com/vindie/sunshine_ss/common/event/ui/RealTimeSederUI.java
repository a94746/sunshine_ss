package com.vindie.sunshine_ss.common.event.ui;

import com.vindie.sunshine_ss.queue.dto.QueueElement;

import java.util.List;
import java.util.Map;

public interface RealTimeSederUI {
    List<Long> send(Map<Long, List<QueueElement>> queueElements);
}
