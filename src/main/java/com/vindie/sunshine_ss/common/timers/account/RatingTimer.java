package com.vindie.sunshine_ss.common.timers.account;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.common.dto.Gender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor
@Slf4j
public class RatingTimer {
    public static final int INTERVAL_DAYS = 2;
    private AccountRepo accountRepo;

    @Transactional
    @Scheduled(fixedRate = INTERVAL_DAYS, timeUnit = TimeUnit.DAYS)
    public void timer() {
        log.info("Start RatingTimer");
        Map<Gender, List<Account>> gender2accounts = accountRepo.findAll()
                .stream()
                .collect(groupingBy(Account::getGender));

        for (List<Account> accounts : gender2accounts.values()) {
            accounts = accounts.stream()
                    .sorted((a1, a2) -> newLikesViews(a2).compareTo(newLikesViews(a1)))
                    .toList();

            final long accountSize = accounts.size();
            for (int i = 0; i < accountSize; i++) {
                var acc = accounts.get(i);
                if (acc.getLikes() >= 3) {
                    acc.setRating(calculateRating(Long.valueOf(i), accountSize));
                }
            }
            accountRepo.saveAll(accounts);
        }
        log.info("End RatingTimer");
    }

    private static double calculateRating(Long num, Long allCount) {
        double result = ((num.doubleValue() + 1) / allCount.doubleValue()) * 100;
        return BigDecimal.valueOf(result)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private static Double newLikesViews(Account acc) {
        return (100D * acc.getLikes()) / acc.getViews();
    }
}
