package com.vindie.sunshine_ss.timer;

import com.vindie.sunshine_ss.common.timers.DailyMatchTimer;
import com.vindie.sunshine_ss.interfaces.WithDbData;
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
    void account_cleaner_timer_test() {
        var matchesBefore = matchRepo.findAll();
        var location = locationRepo.findAll().get(0);
        location.setLastScheduling(LocalDateTime.now().minusDays(2));
        locationRepo.save(location);

        dailyMatchTimer.schTimeFrom = LocalTime.of(0,0,1);
        dailyMatchTimer.schTimeTo = LocalTime.of(23,59,59);
        dailyMatchTimer.timer();

        assertTrue(matchesBefore.size() < matchRepo.findAll().size());
        assertFalse(locationRepo.findById(location.getId()).get().getScheduledNow());
        assertTrue(locationRepo.findById(location.getId()).get().getLastScheduling().isAfter(location.getLastScheduling()));
    }
}
