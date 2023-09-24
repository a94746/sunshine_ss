package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.service.AccountService;
import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Relation;
import com.vindie.sunshine_ss.filter.dto.RelationWithGenders;
import com.vindie.sunshine_ss.scheduling.dto.SchAccount;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.vindie.sunshine_ss.scheduling.rules.Flow.LAST_PRESENCE_LIMIT_HOURS;
import static com.vindie.sunshine_ss.scheduling.rules.Flow.LAST_PRESENCE_LIMIT_HOURS_PREM;

public class SchParser {
    private SchParser() {}

    public static Collection<Pair<Long, Long>> parseFrom(Map<Long, Set<Long>> input) {
        return input.entrySet()
                .stream()
                .map(e -> e.getValue().stream()
                            .map(id -> Pair.of(e.getKey(), id))
                            .toList())
                .flatMap(Collection::stream)
                .toList();
    }

    public static Collection<SchAccount> parseTo(Map<Account, Collection<Long>> accs2avoidMatches) {
        return accs2avoidMatches.entrySet().stream()
                .filter(e -> ChronoUnit.HOURS.between(e.getKey().getLastPresence(), LocalDateTime.now())
                        <= Math.max(LAST_PRESENCE_LIMIT_HOURS, LAST_PRESENCE_LIMIT_HOURS_PREM))
                .map(e -> new SchAccount(
                        e.getKey().getId(),
                        Math.toIntExact(ChronoUnit.HOURS.between(e.getKey().getLastPresence(), LocalDateTime.now())),
                        Math.toIntExact(ChronoUnit.YEARS.between(e.getKey().getBday(), LocalDate.now())),
                        e.getKey().getGender(),
                        e.getKey().getRating(),
                        e.getValue(),
                        AccountService.isPrem(e.getKey()) ? e.getKey().getPremMatchesNum() : e.getKey().getMatchesNum(),
                        AccountService.isPrem(e.getKey()),
                        parseGenders2relations(e.getKey().getFilter().getRelationsWithGenders()),
                        e.getKey().getFilter().getChatPrefs(),
                        e.getKey().getFilter().getAgeFrom(),
                        e.getKey().getFilter().getAgeTo()))
                .toList();
    }

    private static Map<Gender, Set<Relation>> parseGenders2relations(Collection<RelationWithGenders> input) {
        Map<Gender, Set<Relation>> result = new EnumMap<>(Gender.class);
        input.stream()
                .map(r2g -> r2g.getGenders()
                        .stream()
                        .map(g -> Pair.of(g, r2g.getRelation()))
                        .toList())
                .flatMap(Collection::stream)
                .forEach(pair ->
                        result.computeIfAbsent(pair.getFirst(), ignore -> new HashSet<>()).add(pair.getSecond()));
        return result;
    }
}
