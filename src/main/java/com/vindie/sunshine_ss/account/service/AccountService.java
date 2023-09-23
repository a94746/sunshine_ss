package com.vindie.sunshine_ss.account.service;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.match.MatchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private AccountRepo accountRepo;
    private ContactService contactService;
    private CreadService creadService;
    private DeviceService deviceService;
    private MatchService matchService;


    public List<Account> findForScheduling(Long locationId) {
        return accountRepo.findForScheduling(locationId);
    }

    public static boolean isPrem(Account acc) {
        return acc.getPremTill() != null
                && acc.getPremTill().isAfter(LocalDateTime.now());
    }

    public static Account createCorrect() {
        Account acc = new Account();
        acc.setViews(0);
        acc.setLikes(0);
        acc.setRating((byte) 50);
        acc.setDeleted(false);
        acc.setWithoutActualMatches(false);
        return acc;
    }

}
