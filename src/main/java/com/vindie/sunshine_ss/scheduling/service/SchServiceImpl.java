package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.service.AccountService;
import com.vindie.sunshine_ss.match.Match;
import com.vindie.sunshine_ss.match.MatchRepo;
import com.vindie.sunshine_ss.match.MatchService;
import com.vindie.sunshine_ss.scheduling.dto.SchAccount;
import com.vindie.sunshine_ss.scheduling.rules.Flow;
import com.vindie.sunshine_ss.scheduling.rules.SmalRules;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SchServiceImpl implements SchService {
    private AccountService accountService;
    private MatchRepo matchRepo;

    @Override
    @Transactional
    public void runSch(Long locationId) {
        List<Account> accs = accountService.findForScheduling(locationId);
        Map<Long, Account> ids2accs = accs.stream()
                .collect(Collectors.toMap(Account::getId, a -> a));
        Map<Account, Collection<Long>> accs2avoidMatches = new HashMap<>();
        accs.forEach(a -> {
            List<Long> avoidMatches = a.getMatchesOwner().stream()
                    .map(Match::getPartner)
                    .map(Account::getId)
                    .toList();
            accs2avoidMatches.put(a, avoidMatches);
        });
        Collection<SchAccount> parseToResult = SchParser.parseTo(accs2avoidMatches);
        Map<Long, Set<Long>> schResult = calculate(parseToResult);
        List<Match> mathes = SchParser.parseFrom(schResult)
                .stream()
                .map(pair -> {
                    Match match = MatchService.createCorrect();
                    match.setOwner(ids2accs.get(pair.getFirst()));
                    match.setPartner(ids2accs.get(pair.getSecond()));
                    return match;
                })
                .toList();
        matchRepo.saveAll(mathes);
    }

    @Override
    public Map<Long, Set<Long>> calculate(Collection<SchAccount> accounts) {
        for (SchAccount theAccount : accounts) {
            for (SchAccount acc : accounts) {
                if (Flow.FIRST.test(theAccount, acc)) {
                    addMatchEachOther(theAccount, acc);
                    if (SmalRules.ONE_ENOUGH_MATCHES.test(theAccount)) break;
                }
            }
        }
        return accounts.stream()
                .collect(Collectors.toMap(SchAccount::getId,
                        a -> a.getResultMatches().stream()
                                .map(SchAccount::getId)
                                .collect(Collectors.toSet())));
    }

    public void addMatchEachOther(SchAccount acc1, SchAccount acc2) {
        acc1.getResultMatches().add(acc2);
        acc2.getResultMatches().add(acc1);
    }

//    private boolean forTest(SchAccount theAccount,SchAccount acc) {
//        boolean b1 = SmalRules.TWO_NOT_ENOUGH_MATCHES.test(theAccount, acc);
//        boolean b2 = SmalRules.NOT_THE_SAME_ACC.test(theAccount, acc);
//        boolean b3 = SmalRules.NUT_IN_AVOID_MATCHES.test(theAccount, acc);
//        boolean b4 = SmalRules.TWO_SUITABLE_GENDER.test(theAccount, acc);
//        boolean b5 = SmalRules.TWO_SUITABLE_AGE.test(theAccount, acc);
//        boolean b6 = SmalRules.TWO_SUITABLE_LAST_PRESENCE.test(theAccount, acc);
//        boolean b7 = SmalRules.TWO_SUITABLE_RAITING.test(theAccount, acc);
//        return b1 && b2 && b3 && b4 && b5 && b6 && b7;
//    }

}
