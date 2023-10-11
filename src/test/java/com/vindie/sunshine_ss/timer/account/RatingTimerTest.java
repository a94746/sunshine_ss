package com.vindie.sunshine_ss.timer.account;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.base.WithData;
import com.vindie.sunshine_ss.common.timers.account.RatingTimer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
class RatingTimerTest extends WithData {

    @Autowired
    private RatingTimer ratingTimer;

    @Test
    void rating_timer_test() {
        Map<Long, Double> accountsBefore = accountRepo.findAll()
                .stream()
                .collect(toMap(Account::getId, Account::getRating));
        ratingTimer.timer();
        var accountsAfter = accountRepo.findAll();

        assertEquals(accountsBefore.size(), accountsAfter.size());
        accountsAfter.forEach(a -> {
            if (a.getLikes() >= 3) {
                assertNotEquals(accountsBefore.get(a.getId()), a.getRating());
            } else {
                assertEquals(0, a.getRating().compareTo(50D));
            }
        });
    }
}
