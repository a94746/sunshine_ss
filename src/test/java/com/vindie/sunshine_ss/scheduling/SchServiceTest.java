package com.vindie.sunshine_ss.scheduling;

import com.vindie.sunshine_ss.SunshineSsApplicationTests;
import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.scheduling.dto.SchAccount;
import com.vindie.sunshine_ss.scheduling.service.SchParser;
import com.vindie.sunshine_ss.scheduling.service.SchService;
import com.vindie.sunshine_ss.utils.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.vindie.sunshine_ss.utils.DataUtils.RANDOM;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
class SchServiceTest extends SunshineSsApplicationTests {
    private static final int ACCOUNT_NUM = 5_000;
    private static final int MATCHES_PERCENT = 20;

    @Autowired
    SchService schService;

    @Test
    void sch_test () {
        SchParser.parseTo(Collections.emptyMap());
        SchParser.parseFrom(Collections.emptyMap());
        schService.calculate(Collections.emptyList());

        List<Account> accs = new ArrayList<>();
        Map<Account, Collection<Long>> accs2avoidMatches = new HashMap<>();
        for (long i = 0; i < ACCOUNT_NUM; i++) {
            var acc = DataUtils.newTypicalAccount(new Location());
            acc.setId(i);
            accs2avoidMatches.put(acc, new HashSet<>());
            accs.add(acc);
        }

        accs2avoidMatches.entrySet()
                .forEach(entry -> {
                    if (RANDOM.nextInt(100) < MATCHES_PERCENT) {
                        var acc2 = accs2avoidMatches.entrySet().stream()
                                .filter(entry2 -> !entry2.getKey().getId().equals(entry.getKey().getId()))
                                .findAny()
                                .map(entry2 -> {
                                    entry2.getValue().add(entry.getKey().getId());
                                    return entry2;})
                                .map(Map.Entry::getKey)
                                .get();
                        entry.getValue().add(acc2.getId());
                    }
                });
        Instant startSch = Instant.now();
        Collection<SchAccount> parseToResult = SchParser.parseTo(accs2avoidMatches);
        Map<Long, Set<Long>> schResult = schService.calculate(parseToResult);
        Collection<Pair<Long, Long>> parseFromResult = SchParser.parseFrom(schResult);
        long timeSch = Duration.between(startSch, Instant.now()).getSeconds();

        List<Pair<Account, List<Account>>> result = parseFromResult.stream()
                .map(pair -> {
                    var first = accs.stream()
                            .filter(a -> a.getId().equals(pair.getFirst()))
                            .findFirst()
                            .get();
                    var second = accs.stream()
                            .filter(a -> a.getId().equals(pair.getSecond()))
                            .findFirst()
                            .get();
                    return Pair.of(first, second);
                })
                .collect(groupingBy(pair -> pair.getFirst().getId()))
                .values().stream()
                .map(pairs -> {
                    var first = pairs.get(0).getFirst();
                    var second = pairs.stream()
                            .map(Pair::getSecond)
                            .toList();
                    return Pair.of(first, second);
                })
                .toList();
        assertFalse(result.isEmpty());

        log.info("");
        log.info("");
        log.info("-------SCH ACCOUNT_NUM = {}", ACCOUNT_NUM);
        log.info("-------SCH MATCHES_PERCENT = {} %", MATCHES_PERCENT);
        log.info("");
        log.info("-------SCH succeed = {} accounts, {} %", result.size(), (100 * result.size()) / ACCOUNT_NUM);
        log.info("-------SCH timeSch = {} sec", timeSch);
    }
}
