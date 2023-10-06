package com.vindie.sunshine_ss.match;

import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.account.repo.ContactRepo;
import com.vindie.sunshine_ss.common.record.UiLike;
import com.vindie.sunshine_ss.common.record.event.ss.CoupleLikedMatchSsEvent;
import com.vindie.sunshine_ss.common.record.event.ss.SingleLikedMatchSsEvent;
import com.vindie.sunshine_ss.match.record.UiDailyMatch;
import com.vindie.sunshine_ss.match.record.UiLikedMatch;
import com.vindie.sunshine_ss.pic.PicService;
import com.vindie.sunshine_ss.security.record.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vindie.sunshine_ss.account.service.AccountService.isPrem;

@Service
@AllArgsConstructor
@Slf4j
public class MatchService {
    public static final Duration ACTUAL_MATCHES_DURATION = Duration.ofDays(1);
    public static final Duration LIKED_ACTUAL_MATCHES_DURATION = Duration.ofDays(5);
    private MatchRepo matchRepo;
    private AccountRepo accountRepo;
    private PicService picService;
    private ContactRepo contactRepo;
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public boolean like(UiLike uiLike, User user) {
        var pair = matchRepo.getPair(uiLike.getPairId());
        var partnerMatch = pair.stream()
                .filter(m -> !m.getOwner().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow();
        var myMatch = pair.stream()
                .filter(m -> m.getOwner().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow();
        accountRepo.incrementViews(List.of(partnerMatch.getOwner().getId()));
        myMatch.setLiked(true);
        myMatch.setMessage(user.isPrem() ? uiLike.getMessage() : null);
        matchRepo.save(myMatch);

        if (Boolean.TRUE.equals(partnerMatch.getLiked())) {
            var event = new CoupleLikedMatchSsEvent(myMatch.getPartner().getId(), user.getId());
            eventPublisher.publishEvent(event);
            return true;
        } else {
            var event = new SingleLikedMatchSsEvent(user.getId(), myMatch.getPartner().getId());
            eventPublisher.publishEvent(event);
            return false;
        }
    }

    public List<UiDailyMatch> getDaily(User user) {
        return getActualMatches(user).stream()
                .map(m -> {
                    var partner = m.getPartner();
                    UiDailyMatch ui = new UiDailyMatch();
                    ui.setPairId(m.getPairId());
                    ui.setName(partner.getName());
                    ui.setAge(Period.between(partner.getBday(), LocalDate.now()).getYears());
                    ui.setDescription(partner.getDescription());
                    ui.setGender(partner.getGender());
                    ui.setLikedByYou(m.getLiked());
                    ui.setLikedYou(null);
                    ui.setMessage(null);
                    if (user.isPrem() || isPrem(partner)) {
                        var another = getAnother(m.getPairId(), m.getId());
                        if (user.isPrem()) {
                            ui.setLikedYou(another.getLiked());
                        }
                        if (isPrem(partner)) {
                            ui.setMessage(another.getMessage());
                        }
                    }
                    ui.setPicInfos(picService.getPicInfosByOwnerId(partner.getId()));
                    ui.setChatPrefs(partner.getFilter() == null ? null : partner.getFilter().getChatPrefs());

                    if (partner.getFilter() != null) {
                        partner.getFilter().getRelationsWithGenders()
                                .forEach(r2g -> r2g.getGenders()
                                        .stream()
                                        .filter(g -> user.getGender() == g)
                                        .forEach(g -> ui.getRelations().add(r2g.getRelation())));
                    }
                    return ui;
                })
                .toList();
    }

    public List<UiLikedMatch> getLiked(User user) {
        return getBothLikedActualMatches(user).stream()
                .map(m -> {
                    var partner = m.getPartner();
                    UiLikedMatch ui = new UiLikedMatch();
                    ui.setName(partner.getName());
                    ui.setAge(Period.between(partner.getBday(), LocalDate.now()).getYears());
                    ui.setDescription(partner.getDescription());
                    ui.setGender(partner.getGender());
                    ui.setMessage(null);
                    if (isPrem(partner)) {
                        var another = getAnother(m.getPairId(), m.getId());
                        ui.setMessage(another.getMessage());
                    }
                    ui.setPicInfos(picService.getPicInfosByOwnerId(partner.getId()));
                    ui.setChatPrefs(partner.getFilter() == null ? null : partner.getFilter().getChatPrefs());

                    if (partner.getFilter() != null) {
                        partner.getFilter().getRelationsWithGenders()
                                .forEach(r2g -> r2g.getGenders()
                                        .stream()
                                        .filter(g -> user.getGender() == g)
                                        .forEach(g -> ui.getRelations().add(r2g.getRelation())));
                    }
                    contactRepo.findAllByOwnerId(partner.getId())
                            .forEach(c -> ui.getContacts().put(c.getKey(), c.getValue()));
                    return ui;
                })
                .toList();
    }

    public List<Match> getActualMatches(User user) {
        return matchRepo.findAllByOwnerIdAndDateAfter(user.getId(),
                LocalDateTime.now().minus(ACTUAL_MATCHES_DURATION));
    }

    public List<Match> getPair(String pairId) {
        return matchRepo.getPair(pairId);
    }

    public Match getAnother(String pairId, Long thisId) {
        return matchRepo.getAnother(pairId, thisId);
    }

    public List<Match> getBothLikedActualMatches(User user) {
        var list = matchRepo.findAllLikedByPartnerIdAndDateAfter(user.getId(),
                LocalDateTime.now().minus(LIKED_ACTUAL_MATCHES_DURATION));

        return matchRepo.findAllLikedByOwnerIdAndDateAfter(user.getId(),
                LocalDateTime.now().minus(LIKED_ACTUAL_MATCHES_DURATION))
                .stream()
                .filter(m -> list.stream().anyMatch(m2 -> m.getPairId().equals(m2.getPairId())))
                .toList();
    }

    public Map<Long, List<Long>> findAllByOwnerIds(List<Long> ownerIds) {
        Map<Long, List<Long>> result = new HashMap<>();
        matchRepo.findAllByOwnerIds(ownerIds)
                .forEach(match ->
                        result.computeIfAbsent(match.getOwner().getId(), ignore -> new ArrayList<>())
                                .add(match.getPartner().getId()));
        return result;
    }

    public static Match createCorrect() {
        Match match = new Match();
        match.setLiked(false);
        return match;
    }

}
