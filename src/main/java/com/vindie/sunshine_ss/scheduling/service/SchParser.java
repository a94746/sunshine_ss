package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.service.AccountService;
import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.service.properties.PropertiesService;
import com.vindie.sunshine_ss.filter.dto.RelationWithGenders;
import com.vindie.sunshine_ss.match.Match;
import com.vindie.sunshine_ss.match.MatchService;
import com.vindie.sunshine_ss.scheduling.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class SchParser {

    private AccountService accountService;
    private PropertiesService properties;

    public SchRequest getSchRequest(Map<Account, Collection<Long>> accounts2avoidMatches) {
        Collection<SchAccount> accounts = accounts2avoidMatches.entrySet().stream()
                .filter(account2avoidMatches -> isAcceptableLastPresence(account2avoidMatches.getKey()))
                .map(this::toSchAccount)
                .toList();

        SchProperties schProperties = new SchProperties();
        schProperties.setRatingBoundNotPrem(properties.ratingBound);
        schProperties.setRatingBoundPrem(properties.ratingBoundPrem);
        schProperties.setLastPresenceLimitNotPrem(properties.lastPresenceLimit);
        schProperties.setLastPresenceLimitPrem(properties.lastPresenceLimitPrem);
        return new SchRequest(accounts, schProperties);
    }

    public Map<Account, Collection<Long>> getAvoidMatches(Collection<Account> accounts) {
        return accounts.stream()
                .collect(Collectors.toMap(Function.identity(), this::getAvoidMatches));
    }

    public Set<Long> getSucceedAccountIds(Collection<SchMatch> schMatches) {
        return schMatches.stream()
                .flatMap(m -> Stream.of(m.getFirstAccountId(), m.getSecondAccountId()))
                .collect(Collectors.toSet());
    }

    public Collection<Match> toMatches(Collection<SchMatch> schMatches) {
        return schMatches.stream()
                .map(this::toMatches)
                .flatMap(Collection::stream)
                .toList();
    }

    private Collection<Long> getAvoidMatches(Account account) {
        return account.getMatchesOwner().stream()
                .map(Match::getPartner)
                .map(Account::getId)
                .distinct()
                .toList();
    }

    private boolean isAcceptableLastPresence(Account account) {
        final Duration lastPresence = Duration.between(account.getLastPresence(), LocalDateTime.now());
        return AccountService.isPrem(account)
                ? properties.lastPresenceLimitPrem.minus(lastPresence).isPositive()
                : properties.lastPresenceLimit.minus(lastPresence).isPositive();
    }

    private Map<SchGender, Set<SchRelation>> parseGenders2relations(Collection<RelationWithGenders> input) {
        Map<SchGender, Set<SchRelation>> result = new EnumMap<>(SchGender.class);
        input.forEach(r2g -> r2g.getGenders()
                .forEach(g -> result.computeIfAbsent(g.toSchGender(),
                        ignore -> new HashSet<>()).add(r2g.getRelation().toSchRelation())));
        return result;
    }

    private Set<SchChatPref> toSchChatPrefs(Collection<ChatPref> input) {
        return input.stream()
                .map(ChatPref::toSchChatPref)
                .collect(Collectors.toSet());
    }

    private SchAccount toSchAccount(Map.Entry<Account, Collection<Long>> account2avoidMatches) {
        final Account account = account2avoidMatches.getKey();
        final Collection<Long> avoidMatches = account2avoidMatches.getValue();
        return new SchAccount(
                account.getId(),
                Math.toIntExact(ChronoUnit.HOURS.between(account.getLastPresence(), LocalDateTime.now())),
                Math.toIntExact(ChronoUnit.YEARS.between(account.getBday(), LocalDate.now())),
                account.getGender().toSchGender(),
                account.getRating().floatValue(),
                avoidMatches,
                AccountService.isPrem(account) ? account.getPremMatchesNum() : account.getMatchesNum(),
                AccountService.isPrem(account),
                parseGenders2relations(account.getFilter().getRelationsWithGenders()),
                toSchChatPrefs(account.getFilter().getChatPrefs()),
                account.getFilter().getAgeFrom(),
                account.getFilter().getAgeTo());
    }

    private Collection<Match> toMatches(SchMatch schMatch) {
        ArrayList<Match> result = new ArrayList<>(2);
        final String pairId = UUID.randomUUID().toString();
        Account firstAccount = accountService.getReferenceById(schMatch.getFirstAccountId());
        Account secondAccount = accountService.getReferenceById(schMatch.getSecondAccountId());

        Match match1 = MatchService.createCorrect();
        match1.setOwner(firstAccount);
        match1.setPartner(secondAccount);
        match1.setPairId(pairId);
        result.add(match1);

        Match match2 = MatchService.createCorrect();
        match2.setOwner(secondAccount);
        match2.setPartner(firstAccount);
        match2.setPairId(pairId);
        result.add(match2);

        return result;
    }
}
