package com.vindie.sunshine_ss.common.timers.account;

import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.account.repo.DeviceRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class AccountCleanerTimer {
    public static final int INTERVAL_HOURS = 6;
    public static final int TTL_DAYS = 90;

    private AccountRepo accountRepo;
    private DeviceRepo deviceRepo;

    @Transactional
    @Scheduled(fixedRate = INTERVAL_HOURS, timeUnit = TimeUnit.HOURS)
    public void timer() {
        log.info("Start AccountCleanerTimer");
        LocalDateTime older = LocalDateTime.now().minusDays(TTL_DAYS);
        List<Long> ids = accountRepo.findOlder(older);
        var devices = deviceRepo.findAllByOwnerIdIn(ids);
        devices.forEach(d -> {
            d.setLogoutOwnerId(d.getOwner().getId());
            d.setOwner(null);
        });
        deviceRepo.saveAll(devices);
        accountRepo.deleteAllById(ids);
        log.info("End   AccountCleanerTimer");
    }
}
