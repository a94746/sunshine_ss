package com.vindie.sunshine_ss.timer.account;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.base.WithData;
import com.vindie.sunshine_ss.common.timers.account.AccountCleanerTimer;
import com.vindie.sunshine_ss.common.timers.queue.QueueParserTimer;
import com.vindie.sunshine_ss.queue.dto.EventLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountCleanerTimerTest extends WithData {
    @Autowired
    public AccountCleanerTimer accountCleanerTimer;
    @Autowired
    public QueueParserTimer queueParserTimer;

    @Test
    void account_cleaner_timer_test() {
        Account acc2 = accountRepo.save(dataUtils.newTypicalAccount(account.getLocation(), false));
        assertTrue(accountRepo.findById(acc2.getId()).isPresent());
        EventLine eventLine = dataUtils
                .newTypicalEventLine(account.getId(), null, false, true);
        eventLineRepo.save(eventLine);
        queueParserTimer.timer();
        assertEquals(1, eventLineRepo.findAll().size());

        account.setLastPresence(LocalDateTime.now().minusDays(AccountCleanerTimer.TTL_DAYS + 1));
        accountRepo.save(account);
        var devicesBefore = deviceRepo.findAllByOwnerId(account.getId());
        assertFalse(devicesBefore.isEmpty());

        accountCleanerTimer.timer();

        assertTrue(accountRepo.findById(acc2.getId()).isPresent());
        assertFalse(accountRepo.findById(account.getId()).isPresent());

        assertTrue(filterRepo.findByOwnerId(account.getId()).isEmpty());
        assertTrue(matchRepo.findAllByOwnerId(account.getId()).isEmpty());
        assertTrue(deviceRepo.findAllByOwnerId(account.getId()).isEmpty());
        assertEquals(devicesBefore.size(), deviceRepo.findAllByLogoutOwnerId(account.getId()).size());
        assertTrue(contactRepo.findAllByOwnerId(account.getId()).isEmpty());
        assertTrue(picService.getPicInfosByOwnerId(account.getId()).isEmpty());
    }
}
