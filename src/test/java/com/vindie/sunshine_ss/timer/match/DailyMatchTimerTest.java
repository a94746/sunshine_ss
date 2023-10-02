package com.vindie.sunshine_ss.timer.match;

import com.vindie.sunshine_ss.base.WithDbData;
import com.vindie.sunshine_ss.common.event.ss.DailyMatchesSsEvent;
import com.vindie.sunshine_ss.common.event.ss.SsEvent;
import com.vindie.sunshine_ss.common.timers.match.DailyMatchTimer;
import com.vindie.sunshine_ss.location.LocationRepo;
import com.vindie.sunshine_ss.match.MatchRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DailyMatchTimerTest extends WithDbData {
    @Autowired
    public DailyMatchTimer dailyMatchTimer;
    @Autowired
    private MatchRepo matchRepo;
    @Autowired
    private LocationRepo locationRepo;

    @Test
    void daily_match_timer_test() {
        var matchesBefore = matchRepo.findAll();
        var location = locationRepo.findAll().get(0);
        location.setLastScheduling(LocalDateTime.now().minusDays(2));
        locationRepo.save(location);

        dailyMatchTimer.schTimeFrom = LocalTime.of(0,0,1);
        dailyMatchTimer.schTimeTo = LocalTime.of(23,59,59);
        dailyMatchTimer.timer();

        checkEverySec(30).until(() -> matchesBefore.size() < matchRepo.findAll().size());
        assertTrue(locationRepo.findById(location.getId()).get().getLastScheduling().isAfter(location.getLastScheduling()));

        checkEverySec().until(() -> eventEquals(SsEvent.Type.DAILY_MATCHES));
        DailyMatchesSsEvent event = (DailyMatchesSsEvent) getEventAndClean();
        assertFalse(event.getAccIds().isEmpty());
    }

    @Test
    void accs_without_filter() {
        var accs = accountRepo.findAll();
        accs.forEach(a -> a.setFilter(null));
        accountRepo.saveAll(accs);
        var matchesBefore = matchRepo.findAll();
        var location = locationRepo.findAll().get(0);
        location.setLastScheduling(LocalDateTime.now().minusDays(2));
        locationRepo.save(location);

        dailyMatchTimer.schTimeFrom = LocalTime.of(0,0,1);
        dailyMatchTimer.schTimeTo = LocalTime.of(23,59,59);
        dailyMatchTimer.timer();

        checkEverySec().until(() -> eventEquals(SsEvent.Type.DAILY_MATCHES));
        assertTrue(locationRepo.findById(location.getId()).get().getLastScheduling().isAfter(location.getLastScheduling()));
        checkInSec(2, () -> matchesBefore.size() == matchRepo.findAll().size());
        DailyMatchesSsEvent event = (DailyMatchesSsEvent) getEventAndClean();
        assertTrue(event.getAccIds().isEmpty());
    }
}
