package com.vindie.sunshine_ss.timer.match;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.base.WithData;
import com.vindie.sunshine_ss.common.timers.match.MatchCleanerTimer;
import com.vindie.sunshine_ss.match.Match;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class MatchCleanerTimerTest extends WithData {
    @Autowired
    public MatchCleanerTimer matchCleanerTimer;

    @Test
    void match_cleaner_timer_test() {
        Account acc1 = accountRepo.save(dataUtils.newTypicalAccount(account.getLocation(), false));
        Account acc2 = accountRepo.save(dataUtils.newTypicalAccount(account.getLocation(), false));
        String pairId = UUID.randomUUID().toString();
        Match match1 = dataUtils.newTypicalMatch(acc1, acc2, pairId);
        Match match2 = dataUtils.newTypicalMatch(acc2, acc1, pairId);
        matchRepo.saveAll(List.of(match1, match2));
        List<Match> matches = matchRepo.findAll()
                .stream()
                .filter(m -> m.getOwner().getId().equals(acc1.getId()) || m.getOwner().getId().equals(acc2.getId()))
                .toList();
        assertEquals(2, matches.size());
        matches.get(0).setDate(LocalDateTime.now().minus(properties.ttl.matchTTL.plusDays(1)));
        matchRepo.save(matches.get(0));

        matchCleanerTimer.timer();
        assertTrue(matchRepo.findAll().contains(matches.get(1)));
        assertFalse(matchRepo.findAll().contains(matches.get(0)));
        assertTrue(accountRepo.findById(acc1.getId()).isPresent());
        assertTrue(accountRepo.findById(acc2.getId()).isPresent());
    }
}
