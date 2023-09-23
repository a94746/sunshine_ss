package com.vindie.sunshine_ss.common.timers;

import com.vindie.sunshine_ss.common.ss_event.DailyMatchesSsEvent;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.location.LocationRepo;
import com.vindie.sunshine_ss.scheduling.service.SchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * тут мы переносим внесённые вручную записи в таблицу eventLine в таблицу Queue
 */
@Service
@AllArgsConstructor
@Slf4j
public class DailyMatchTimer {
    public static final int INTERVAL_MIN = 11;
    public LocalTime schTimeFrom = LocalTime.of(6,0,0);
    public LocalTime schTimeTo = LocalTime.of(8,0,0);
    public static final long DIFFERENCE = ChronoUnit.HOURS.between(schTimeFrom, schTimeTo);

    private LocationRepo locationRepo;
    private SchService schService;
    private ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedRate = INTERVAL_MIN, timeUnit = TimeUnit.MINUTES)
    public void timer() {
        List<Location> locations = locationRepo.findAll()
                .stream()
                .filter(l -> {
                    var timeThere = LocalTime.now().plusHours(l.getTimeShift());
                    return timeThere.isAfter(schTimeFrom)
                            && timeThere.isBefore(schTimeTo)
                            && !l.getScheduledNow()
                            && ChronoUnit.HOURS.between(l.getLastScheduling(), LocalDateTime.now()) > DIFFERENCE + 1;
                })
                .toList();
        locations.forEach(l -> l.setScheduledNow(true));
        locationRepo.saveAll(locations);

        locations.forEach(l -> {
            schService.runSch(l.getId());
            l.setScheduledNow(false);
            l.setLastScheduling(LocalDateTime.now());
            DailyMatchesSsEvent event = new DailyMatchesSsEvent(l.getId());
            eventPublisher.publishEvent(event);
        });
        locationRepo.saveAll(locations);
    }
}
