package com.vindie.sunshine_ss.match;

import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.account.repo.ContactRepo;
import com.vindie.sunshine_ss.common.dto.UiKey;
import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import com.vindie.sunshine_ss.common.record.event.ss.CoupleLikedMatchSsEvent;
import com.vindie.sunshine_ss.common.record.event.ss.SingleLikedMatchSsEvent;
import com.vindie.sunshine_ss.common.record.event.ss.SsEvent;
import com.vindie.sunshine_ss.common.service.PropertiesService;
import com.vindie.sunshine_ss.pic.PicService;
import com.vindie.sunshine_ss.security.record.User;
import com.vindie.sunshine_ss.ui_dto.UiContact;
import com.vindie.sunshine_ss.ui_dto.UiDailyMatch;
import com.vindie.sunshine_ss.ui_dto.UiLike;
import com.vindie.sunshine_ss.ui_dto.UiLikedMatch;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;

import static com.vindie.sunshine_ss.account.service.AccountService.isPrem;

@Service
@AllArgsConstructor
@Slf4j
public class MatchService {
    private MatchRepo matchRepo;
    private AccountRepo accountRepo;
    private PicService picService;
    private ContactRepo contactRepo;
    private ApplicationEventPublisher eventPublisher;
    private PropertiesService properties;

    @Transactional
    public boolean like(UiLike uiLike, User user) {
        var pairMatсh = getPair(uiLike.getPairId());
        var userMatch = getUserMatch(pairMatсh, user);
        var partnerMatch = getPartnerMatch(pairMatсh, user);

        if (userMatch.getDate().isBefore(LocalDateTime.now().minus(properties.actualMatchesTTL)))
            throw new SunshineException(UiKey.MATCH_NOT_AT_ACTUAL);

        accountRepo.incrementLikes(partnerMatch.getOwner().getId());
        userMatch.setLiked(true);
        userMatch.setMessage(user.isPrem() ? uiLike.getMessage() : null);
        matchRepo.save(userMatch);

        SsEvent event = Boolean.TRUE.equals(partnerMatch.getLiked())
                ? new CoupleLikedMatchSsEvent(userMatch.getPartner().getId(), user.getId())
                : new SingleLikedMatchSsEvent(user.getId(), userMatch.getPartner().getId());
        eventPublisher.publishEvent(event);

        return event instanceof CoupleLikedMatchSsEvent;
    }

    public List<UiDailyMatch> getDaily(User user) {
        return getActualMatches(user).stream()
                .map(m -> toUiDailyMatch(m, user))
                .toList();
    }

    public List<UiLikedMatch> getLiked(User user) {
        return getBothLikedActualMatches(user).stream()
                .map(m -> toUiLikedMatch(m, user))
                .toList();
    }

    public List<Match> getActualMatches(User user) {
        return matchRepo.findAllByOwnerIdAndDateAfter(user.getId(), LocalDateTime.now().minus(properties.actualMatchesTTL));
    }

    public List<Match> getPair(String pairId) {
        return matchRepo.getPair(pairId);
    }

    public Match getAnotherMatch(String pairId, Long thisMatchId) {
        return matchRepo.getAnother(pairId, thisMatchId);
    }

    public List<Match> getBothLikedActualMatches(User user) {
        var list = matchRepo.findAllLikedByPartnerIdAndDateAfter(user.getId(),
                LocalDateTime.now().minus(properties.likedActualMatchesTTL));

        return matchRepo.findAllLikedByOwnerIdAndDateAfter(user.getId(),
                LocalDateTime.now().minus(properties.likedActualMatchesTTL))
                .stream()
                .filter(m -> list.stream().anyMatch(m2 -> m.getPairId().equals(m2.getPairId())))
                .toList();
    }

    public static Match createCorrect() {
        Match match = new Match();
        match.setLiked(false);
        return match;
    }

    private Match getUserMatch(List<Match> twoMatches, User user) {
        return twoMatches.stream()
                .filter(m -> m.getOwner().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("userMatch not present"));
    }

    private Match getPartnerMatch(List<Match> twoMatches, User user) {
        return twoMatches.stream()
                .filter(m -> !m.getOwner().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("partnerMatch not present"));
    }

    private UiLikedMatch toUiLikedMatch(Match match, User user) {
        var partner = match.getPartner();
        UiLikedMatch ui = new UiLikedMatch();
        ui.setName(partner.getName());
        ui.setAge(Period.between(partner.getBday(), LocalDate.now()).getYears());
        ui.setDescription(partner.getDescription());
        ui.setGender(partner.getGender());
        ui.setMessage(null);
        if (isPrem(partner)) {
            var another = getAnotherMatch(match.getPairId(), match.getId());
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
                .forEach(c -> {
                    var uiContact = UiContact.builder()
                            .id(c.getId())
                            .key(c.getKey())
                            .value(c.getValue())
                            .build();
                    ui.getContacts().add(uiContact);
                });
        return ui;
    }

    private UiDailyMatch toUiDailyMatch(Match match, User user) {
        var partner = match.getPartner();
        UiDailyMatch ui = new UiDailyMatch();
        ui.setPairId(match.getPairId());
        ui.setName(partner.getName());
        ui.setAge(Period.between(partner.getBday(), LocalDate.now()).getYears());
        ui.setDescription(partner.getDescription());
        ui.setGender(partner.getGender());
        ui.setLikedByYou(match.getLiked());
        ui.setLikedYou(null);
        ui.setMessage(null);
        if (user.isPrem() || isPrem(partner)) {
            var another = getAnotherMatch(match.getPairId(), match.getId());
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
    }

}
