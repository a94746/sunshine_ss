package com.vindie.sunshine_ss.timer.account;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.common.timers.account.AccountPremCleanerTimer;
import com.vindie.sunshine_ss.interfaces.WithDbData;
import com.vindie.sunshine_ss.utils.DataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountPremCleanerTimerTest extends WithDbData {
    @Autowired
    private AccountPremCleanerTimer accountPremCleanerTimer;

    @Test
    void account_cleaner_timer_test() {
        final byte premMatchesNum = 10;
        Account acc2 = DataUtils.newTypicalAccount(account.getLocation());
        acc2.setPremMatchesNum(premMatchesNum);
        acc2.setPremTill(LocalDateTime.now().minusHours(1));
        accountRepo.save(acc2);
        assertTrue(accountRepo.findById(acc2.getId()).isPresent());
        assertEquals(premMatchesNum, accountRepo.findById(acc2.getId()).get().getPremMatchesNum());

        accountPremCleanerTimer.timer();
        assertTrue(accountRepo.findById(acc2.getId()).isPresent());
        assertNull(accountRepo.findById(acc2.getId()).get().getPremMatchesNum());
        assertNull(accountRepo.findById(acc2.getId()).get().getPremTill());
        assertEquals(accountRepo.findById(acc2.getId()).get().getGender().getMatchesNum(),
                accountRepo.findById(acc2.getId()).get().getMatchesNum());
    }
}
