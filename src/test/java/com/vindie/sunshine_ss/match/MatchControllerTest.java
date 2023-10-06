package com.vindie.sunshine_ss.match;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vindie.sunshine_ss.base.WithMvc;
import com.vindie.sunshine_ss.common.record.UiLike;
import com.vindie.sunshine_ss.common.record.event.ss.CoupleLikedMatchSsEvent;
import com.vindie.sunshine_ss.common.record.event.ss.SingleLikedMatchSsEvent;
import com.vindie.sunshine_ss.match.record.UiDailyMatch;
import com.vindie.sunshine_ss.match.record.UiLikedMatch;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MatchControllerTest extends WithMvc {

    @Test
    void get_daily_test() throws Exception {
        var acc2 = accountRepo.findAll()
                .stream()
                .filter(a -> a.getFilter() != null && !a.getId().equals(account.getId()))
                .findAny()
                .orElseThrow();
        String pairId = UUID.randomUUID().toString();
        matchRepo.save(dataUtils.newTypicalMatch(account, acc2, pairId));
        matchRepo.save(dataUtils.newTypicalMatch(acc2, account, pairId));
        mvc.perform(get("/match/get_daily")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andExpect(modelMatches(new TypeReference<List<UiDailyMatch>>() {}, l -> {
                    var allMathces = matchRepo.findAll();
                        assertEquals(allMathces.stream()
                                .filter(m -> m.getOwner().getId().equals(account.getId()))
                                .count(),
                                l.size());
                }));
    }

    @Test
    void get_liked_test() throws Exception {
        var acc2 = accountRepo.findAll()
                .stream()
                .filter(a -> a.getFilter() != null && !a.getId().equals(account.getId()))
                .findAny()
                .orElseThrow();
        String pairId = UUID.randomUUID().toString();
        matchRepo.save(dataUtils.newTypicalMatch(account, acc2, pairId));
        matchRepo.save(dataUtils.newTypicalMatch(acc2, account, pairId));
        mvc.perform(get("/match/get_liked")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andExpect(modelMatches(new TypeReference<List<UiLikedMatch>>() {},l -> {
                    var allMathces = matchRepo.findAll();
                    assertEquals(allMathces.stream()
                                    .filter(m -> m.getOwner().getId().equals(account.getId()) && m.getLiked()
                                            && matchRepo.getAnother(m.getPairId(), m.getId()).getLiked())
                                    .count(),
                            l.size());
                }));
    }

    @Test
    void like_true_test() throws Exception {
        String message = "UUID.randomUUID().toString()";
        var acc2 = accountRepo.findAll()
                .stream()
                .filter(a -> a.getFilter() != null && !a.getId().equals(account.getId()))
                .findAny()
                .orElseThrow();
        String pairId = UUID.randomUUID().toString();
        var myMatch = dataUtils.newTypicalMatch(account, acc2, pairId);
        myMatch.setLiked(false);
        var partnerMatch = dataUtils.newTypicalMatch(acc2, account, pairId);
        partnerMatch.setLiked(true);
        matchRepo.saveAll(List.of(myMatch, partnerMatch));

        UiLike uiLike = UiLike.builder()
                .pairId(pairId)
                .message(message)
                .build();

        mvc.perform(post("/match/like")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(MAPPER.writeValueAsString(uiLike)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        CoupleLikedMatchSsEvent event = (CoupleLikedMatchSsEvent) getEventAndClean();
        assertEquals(acc2.getId(), event.getFirstLikedId());
        assertEquals(account.getId(), event.getSecondLikedId());
        assertEquals(acc2.getLikes() + 1, accountRepo.findById(acc2.getId()).get().getLikes());
        matchRepo.getPair(pairId)
                .forEach(m -> assertEquals(true, m.getLiked()));
    }

    @Test
    void like_false_test() throws Exception {
        String message = "UUID.randomUUID().toString()";
        var acc2 = accountRepo.findAll()
                .stream()
                .filter(a -> a.getFilter() != null && !a.getId().equals(account.getId()))
                .findAny()
                .orElseThrow();
        String pairId = UUID.randomUUID().toString();
        var myMatch = dataUtils.newTypicalMatch(account, acc2, pairId);
        myMatch.setLiked(false);
        var partnerMatch = dataUtils.newTypicalMatch(acc2, account, pairId);
        partnerMatch.setLiked(false);
        matchRepo.saveAll(List.of(myMatch, partnerMatch));

        UiLike uiLike = UiLike.builder()
                .pairId(pairId)
                .message(message)
                .build();

        mvc.perform(post("/match/like")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(MAPPER.writeValueAsString(uiLike)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
        SingleLikedMatchSsEvent event = (SingleLikedMatchSsEvent) getEventAndClean();
        assertEquals(acc2.getId(), event.getLikedId());
        assertEquals(account.getId(), event.getLikingId());
        assertEquals(acc2.getLikes() + 1, accountRepo.findById(acc2.getId()).get().getLikes());
    }

}
