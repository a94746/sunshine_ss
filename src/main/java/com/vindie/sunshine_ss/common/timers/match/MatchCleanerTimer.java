package com.vindie.sunshine_ss.common.timers.match;

import com.vindie.sunshine_ss.common.service.properties.PropertiesService;
import com.vindie.sunshine_ss.match.MatchRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class MatchCleanerTimer {
    public static final int INTERVAL_HOURS = 7;

    private MatchRepo matchRepo;
    private PropertiesService properties;

    @Transactional
    @Scheduled(fixedRate = INTERVAL_HOURS, timeUnit = TimeUnit.HOURS)
    public void timer() {
        log.info("Start MatchCleanerTimer");
        LocalDateTime older = LocalDateTime.now().minus(properties.ttl.matchTTL);
        matchRepo.deleteOlder(older);
        log.info("End   MatchCleanerTimer");
    }
}
