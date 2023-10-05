package com.vindie.sunshine_ss.match;

import com.vindie.sunshine_ss.match.record.UiDailyMatch;
import com.vindie.sunshine_ss.pic.PicService;
import com.vindie.sunshine_ss.security.record.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private MatchRepo matchRepo;
    private PicService picService;

    public List<UiDailyMatch> getDaily(User user) {
        return getActualMathes(user).stream()
                .map(m -> {
                    var partner = m.getPartner();
                    UiDailyMatch ui = new UiDailyMatch();
                    ui.setId(partner.getId());
                    ui.setName(partner.getName());
                    ui.setAge(Period.between(partner.getBday(), LocalDate.now()).getYears());
                    ui.setDescription(partner.getDescription());
                    ui.setGender(partner.getGender());
                    ui.setLikedYou(user.isPrem() ? m.getLiked() : null);
                    ui.setMessage(isPrem(partner) ? m.getMessage() : null);
                    ui.setPicInfos(picService.getPicInfosByOwnerId(partner.getId()));
                    ui.setChatPrefs(partner.getFilter().getChatPrefs());

                    partner.getFilter().getRelationsWithGenders()
                            .forEach(r2g -> r2g.getGenders()
                                    .stream()
                                    .filter(g -> user.getGender() == g)
                                    .forEach(g -> ui.getRelations().add(r2g.getRelation())));
                    return ui;
                })
                .toList();
    }

    public List<Match> getActualMathes(User user) {
        return matchRepo.findAllByOwnerIdAndDateAfter(user.getId(),
                LocalDateTime.now().minus(ACTUAL_MATCHES_DURATION));
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
        match.setViewed(false);
        return match;
    }

}
