package com.vindie.sunshine_ss.common.timers.queue;

import com.vindie.sunshine_ss.common.event.ui.RealTimeSederUI;
import com.vindie.sunshine_ss.queue.dto.QueueElement;
import com.vindie.sunshine_ss.queue.repo.EventLineRepo;
import com.vindie.sunshine_ss.queue.repo.QueueElementRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class QueueSenderTimer {

    public static final int INTERVAL_MINS = 143;

    private QueueElementRepo queueElementRepo;
    private EventLineRepo eventLineRepo;
    private RealTimeSederUI realTimeSederUI;

    @Transactional
    @Scheduled(fixedRate = INTERVAL_MINS, timeUnit = TimeUnit.MINUTES)
    public void timer() {
        log.info("Start QueueSenderTimer");
        Map<Long, List<QueueElement>> toSend = queueElementRepo.findAllNotifs()
                .stream()
                .collect(Collectors.groupingBy(qe -> qe.getOwner().getId()));
        List<Long> queueElementIds = realTimeSederUI.send(toSend);
        if (CollectionUtils.isEmpty(queueElementIds)) return;
        queueElementRepo.deleteByIdIn(queueElementIds);
        log.info("End QueueSenderTimer");
    }
}
