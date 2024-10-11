package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.account.service.AccountService;
import com.vindie.sunshine_ss.common.metrics.MetricService;
import com.vindie.sunshine_ss.common.record.event.ss.DailyMatchesSsEvent;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.location.LocationRepo;
import com.vindie.sunshine_ss.match.Match;
import com.vindie.sunshine_ss.match.MatchRepo;
import com.vindie.sunshine_ss.match.MatchService;
import com.vindie.sunshine_ss.scheduling.dto.SchAccount;
import com.vindie.sunshine_ss.scheduling.rules.Flow;
import com.vindie.sunshine_ss.scheduling.rules.SmalRules;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SchServiceImpl implements SchService {
    private AccountService accountService;
    private MatchRepo matchRepo;
    private LocationRepo locationRepo;
    private AccountRepo accountRepo;
    private ApplicationEventPublisher eventPublisher;
    private MetricService metricService;

    @Override
    @Async
    @Transactional
    public void runSch(Location location) {
        Instant start = Instant.now();
        LocalTime timeThere = LocalTime.now().plusHours(location.getTimeShift());
        log.info("Start runSch for: {}. Time there: {}:{}", location.getName(), timeThere.getHour(), timeThere.getMinute());
        location.setLastScheduling(LocalDateTime.now());
        locationRepo.save(location);

        List<Account> accs = accountService.findForScheduling(location.getId());
        Map<Long, Account> ids2accs = accs.stream()
                .collect(Collectors.toMap(Account::getId, a -> a));
        Map<Account, Collection<Long>> accs2avoidMatches = new HashMap<>();
        accs.forEach(a -> {
            List<Long> avoidMatches = a.getMatchesOwner().stream()
                    .map(Match::getPartner)
                    .map(Account::getId)
                    .distinct()
                    .toList();
            accs2avoidMatches.put(a, avoidMatches);
        });
        Collection<SchAccount> parseToResult = SchParser.parseTo(accs2avoidMatches);
        Map<Long, Map<Long, String>> schResult = calculate(parseToResult);
        List<Match> mathes = SchParser.parseFrom(schResult)
                .stream()
                .map(triple -> {
                    Match match = MatchService.createCorrect();
                    match.setOwner(ids2accs.get(triple.getFirst()));
                    match.setPartner(ids2accs.get(triple.getSecond()));
                    match.setPairId(triple.getThird());
                    return match;
                })
                .toList();
        DailyMatchesSsEvent event = new DailyMatchesSsEvent(schResult.keySet());
        eventPublisher.publishEvent(event);
        matchRepo.saveAll(mathes);
        accountRepo.incrementViews(schResult.keySet());


        Double durationSec = Duration.between(start, Instant.now()).getNano() * 0.000000001D;
        log.info("End   runSch for: {} in {} sec", location.getName(), durationSec);
        int peopleNum = accountRepo.countByLocationId(location.getId());
        metricService.setPeopleNum(location.getId(), peopleNum);
        metricService.setPeoplePercentHaveMatches(location.getId(), peopleNum == 0 ? 0 : ((schResult.size() * 100D) / peopleNum));
        double avgMatches = schResult.values().stream()
                .mapToInt(Map::size)
                .average()
                .orElse(0);
        metricService.setNumOfMatchesPerPerson(location.getId(), avgMatches);
        metricService.setSchedulingTimeSec(location.getId(), durationSec);
    }

    @Override
    public Map<Long, Map<Long, String>> calculate(Collection<SchAccount> accounts) {
        for (SchAccount theAccount : accounts) {
            for (SchAccount acc : accounts) {
                if (Flow.FIRST.test(theAccount, acc)) {
                    addMatchEachOther(theAccount, acc);
                    if (SmalRules.ONE_ENOUGH_MATCHES.test(theAccount)) break;
                }
            }
        }
        return accounts.stream()
                .collect(Collectors.toMap(SchAccount::getId, SchAccount::getResultMatches));
    }

    public void addMatchEachOther(SchAccount acc1, SchAccount acc2) {
        String pairId = UUID.randomUUID().toString();
        acc1.getResultMatches().put(acc2.getId(), pairId);
        acc2.getResultMatches().put(acc1.getId(), pairId);
    }

    private boolean forDebugOnly(SchAccount theAccount, SchAccount  acc) {
        boolean b1 = SmalRules.TWO_NOT_ENOUGH_MATCHES.test(theAccount, acc);
        boolean b2 = SmalRules.NOT_THE_SAME_ACC.test(theAccount, acc);
        boolean b3 = SmalRules.NUT_IN_AVOID_MATCHES.test(theAccount, acc);
        boolean b4 = SmalRules.TWO_SUITABLE_GENDER.test(theAccount, acc);
        boolean b5 = SmalRules.TWO_SUITABLE_AGE.test(theAccount, acc);
        boolean b6 = SmalRules.TWO_SUITABLE_LAST_PRESENCE.test(theAccount, acc);
        boolean b7 = SmalRules.TWO_SUITABLE_RAITING.test(theAccount, acc);
        return b1 && b2 && b3 && b4 && b5 && b6 && b7;
    }

}
