package com.vindie.sunshine_ss.timer.account;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.base.WithData;
import com.vindie.sunshine_ss.common.timers.account.AccountPremCleanerTimer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountPremCleanerTimerTest extends WithData {
    @Autowired
    private AccountPremCleanerTimer accountPremCleanerTimer;

    @Test
    void account_cleaner_timer_test() {
        final byte premMatchesNum = 10;
        Account acc2 = dataUtils.newTypicalAccount(account.getLocation(), false);
        acc2.setPremMatchesNum(premMatchesNum);
        acc2.setPremTill(LocalDateTime.now().minusHours(1));
        accountRepo.save(acc2);
        assertTrue(accountRepo.findById(acc2.getId()).isPresent());
        assertEquals(premMatchesNum, accountRepo.findById(acc2.getId()).get().getPremMatchesNum());

        accountPremCleanerTimer.timer();
        assertTrue(accountRepo.findById(acc2.getId()).isPresent());
        assertNull(accountRepo.findById(acc2.getId()).get().getPremMatchesNum());
        assertNull(accountRepo.findById(acc2.getId()).get().getPremTill());
        assertEquals(properties.genderMatchNum.getMatchMaxNum(accountRepo.findById(acc2.getId()).get().getGender(), false),
                accountRepo.findById(acc2.getId()).get().getMatchesNum());
    }
}
