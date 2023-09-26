package com.vindie.sunshine_ss.common.timers.account;

import com.vindie.sunshine_ss.account.repo.AccountRepo;
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
public class AccountPremCleanerTimer {

    public static final int INTERVAL_HOURS = 2;

    private AccountRepo accountRepo;

    @Transactional
    @Scheduled(fixedRate = INTERVAL_HOURS, timeUnit = TimeUnit.HOURS)
    public void timer() {
        log.info("Start AccountPremCleanerTimer");
        LocalDateTime now = LocalDateTime.now();
        accountRepo.deleteOverduePrem(now);
        log.info("End   AccountPremCleanerTimer");
    }
}
