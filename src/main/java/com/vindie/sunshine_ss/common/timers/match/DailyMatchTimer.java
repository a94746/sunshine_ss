package com.vindie.sunshine_ss.common.timers.match;

import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.location.LocationRepo;
import com.vindie.sunshine_ss.scheduling.service.SchLockByLocation;
import com.vindie.sunshine_ss.scheduling.service.SchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class DailyMatchTimer {
    public static final int INTERVAL_MIN = 30;
    public static LocalTime schTimeFrom = LocalTime.of(6,0,0);
    public static LocalTime schTimeTo = LocalTime.of(8,0,0);

    private LocationRepo locationRepo;
    private SchService schService;

    @Scheduled(fixedRate = INTERVAL_MIN, timeUnit = TimeUnit.MINUTES)
    public void timer() {
        log.info("Start DailyMatchTimer");
        final LocalDateTime now = LocalDateTime.now();
        List<Location> locations = locationRepo.findAll()
                .stream()
                .filter(l -> {
                    var timeThere = now.plusHours(l.getTimeShift());
                    return timeThere.toLocalTime().isAfter(schTimeFrom)
                            && timeThere.toLocalTime().isBefore(schTimeTo)
                            && ChronoUnit.HOURS.between(l.getLastScheduling(), LocalDateTime.now()) > 22
                            && !SchLockByLocation.isLocked(l.getId());
                })
                .toList();

        locations.forEach(schService::runSch);
    }
}
